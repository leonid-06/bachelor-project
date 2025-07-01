package com.leonid.workerbot.service.ssm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class SsmService {

    private static final String DOCUMENT_NAME = "AWS-RunShellScript";
    private static final String PARAMETER_COMMANDS_NAME = "commands";
    private static final Duration POOLING_INTERVAL = Duration.ofSeconds(1);
    private static final Duration INITIAL_DELAY = Duration.ofSeconds(4);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();



    private final SsmClient ssmClient;

    public boolean send(List<String> base64Commands, String instanceId) {

        SendCommandRequest commandRequest = SendCommandRequest.builder()
                .instanceIds(instanceId)
                .documentName(DOCUMENT_NAME)
                .parameters(Map.of(PARAMETER_COMMANDS_NAME, base64Commands))
                .build();

        SendCommandResponse commandResponse = ssmClient.sendCommand(commandRequest);
        String commandId = commandResponse.command().commandId();

        System.out.println("Command sent, ID: " + commandId);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        GetCommandInvocationRequest invocationRequest = GetCommandInvocationRequest.builder()
                .commandId(commandId)
                .instanceId(instanceId)
                .build();

        GetCommandInvocationResponse invocationResponse =
                ssmClient.getCommandInvocation(invocationRequest);

        System.out.println("Command output:");
        System.out.println(invocationResponse.standardOutputContent());

        if (!invocationResponse.standardErrorContent().isEmpty()) {
            System.out.println("Errors:");
            System.out.println(invocationResponse.standardErrorContent());
        }

        return true;
    }


    // old
//    public CompletableFuture<InstanceInformation> waitForSsm(String instanceId, Duration timeout) {
//        CompletableFuture<InstanceInformation> future = new CompletableFuture<>();
//        long deadline = System.currentTimeMillis() + timeout.toMillis();
//
//        Runnable pollTask = () -> {
//            try {
//                var response = ssmClient.describeInstanceInformation(DescribeInstanceInformationRequest.builder().build());
//
//                response.instanceInformationList().stream()
//                        .filter(info -> instanceId.equals(info.instanceId()))
//                        .filter(info -> "Online".equalsIgnoreCase(info.pingStatusAsString()))
//                        .findFirst()
//                        .ifPresentOrElse(
//                                info -> {
//                                    future.complete(info);
//                                    executor.shutdown(); // Stop polling
//                                },
//                                () -> {
//                                    if (System.currentTimeMillis() >= deadline) {
//                                        future.completeExceptionally(new TimeoutException("SSM not available within timeout"));
//                                        executor.shutdown();
//                                    }
//                                }
//                        );
//            } catch (SsmException e) {
//                future.completeExceptionally(e);
//                executor.shutdown();
//            }
//        };
//
//        executor.scheduleAtFixedRate(pollTask, INITIAL_DELAY.toMillis(), POOLING_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
//        return future;
//    }
//
//    public CompletableFuture<Void> waitForSetup(String instanceId) {
//        String paramName = "/setup/" + instanceId;
//        CompletableFuture<Void> future = new CompletableFuture<>();
//
//        ScheduledFuture<?> scheduledTask = executor.scheduleAtFixedRate(() -> {
//            try {
//                GetParameterRequest request = GetParameterRequest.builder()
//                        .withDecryption(false)
//                        .name(paramName)
//                        .build();
//
//                GetParameterResponse response = ssmClient.getParameter(request);
//                String value = response.parameter().value();
//
//                System.out.println("Parameter: " + value);
//                future.complete(null);
//            } catch (SsmException e) {
//                if (e.awsErrorDetails().errorCode().equals("ParameterNotFound")) {
//                    System.out.println("No parameter");
//                } else {
//                    System.err.println("Unexpected error: " + e.awsErrorDetails().errorMessage());
//                    future.completeExceptionally(e);
//                }
//            }
//        }, 25, 1, TimeUnit.SECONDS);
//
//        future.whenComplete((res, ex) -> scheduledTask.cancel(true));
//
//        return future;
//    }






    public CompletableFuture<Void> waitForSetup(String instanceId) {
        return CompletableFuture.runAsync(() -> {
            String paramName = "/setup/" + instanceId;
            while (true) {
                try {
                    GetParameterRequest request = GetParameterRequest.builder()
                            .withDecryption(false)
                            .name(paramName)
                            .build();

                    GetParameterResponse response = ssmClient.getParameter(request);
                    String value = response.parameter().value();

                    System.out.println("Parameter: " + value);
                    break; // Параметр найден — выходим из цикла

                } catch (SsmException e) {
                    if (e.awsErrorDetails().errorCode().equals("ParameterNotFound")) {
                        System.out.println("No parameter yet, waiting...");
                    } else {
                        throw new RuntimeException("Unexpected error while waiting for setup parameter", e);
                    }
                }

                try {
                    Thread.sleep(1000); // Ждем 1 секунду перед следующей попыткой
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for setup parameter", e);
                }
            }
        });
    }


    public CompletableFuture<InstanceInformation> waitForSsm(String instanceId, Duration timeout) {
        return CompletableFuture.supplyAsync(() -> {
            long deadline = System.currentTimeMillis() + timeout.toMillis();

            while (System.currentTimeMillis() < deadline) {
                try {
                    var response = ssmClient.describeInstanceInformation(DescribeInstanceInformationRequest.builder().build());

                    var maybeInfo = response.instanceInformationList().stream()
                            .filter(info -> instanceId.equals(info.instanceId()))
                            .filter(info -> "Online".equalsIgnoreCase(info.pingStatusAsString()))
                            .findFirst();

                    if (maybeInfo.isPresent()) {
                        return maybeInfo.get();
                    }

                } catch (SsmException e) {
                    throw new RuntimeException("Error while waiting for SSM", e);
                }

                try {
                    Thread.sleep(1000); // Ждем 1 секунду
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for SSM", e);
                }
            }

            throw new RuntimeException("SSM not available within timeout");
        });
    }




}
