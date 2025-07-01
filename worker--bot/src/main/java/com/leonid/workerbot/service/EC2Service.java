package com.leonid.workerbot.service;

import com.leonid.workerbot.model.Instance;
import com.leonid.workerbot.repo.InstanceRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.time.Duration;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EC2Service {

    private final Ec2Client ec2Client;
    private final S3Service s3Service;
    private final InstanceRepo instanceRepo;


    @Value("${amazon.machine.image}")
    private String instanceId;
    @Value("${security.group.id}")
    private String securityGroupId;
    @Value("${pem.key.name}")
    private String keyName;
    @Value("${s3.bucket.name}")
    private String bucketName;

    @Value("${iam.role.name}")
    private String iamRoleName;

    /**
     * Make request to create ec2 instances.
     * Please note, despite the fact that IP field is in Instance class,
     * the IP address is not returned yet.
     *
     * @return instance ids in format like i-08849xxx35c597a6b
     */
    public String sendRequestToCreateInstance(String scriptText, String instanceName) {
        log.info("Starting instance creation");

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId(instanceId)
                .instanceType(InstanceType.T2_MICRO)
                .securityGroupIds(securityGroupId)
                .keyName(keyName)
                .minCount(1)
                .maxCount(1)
                .userData(Base64.getEncoder().encodeToString(scriptText.getBytes()))
                .iamInstanceProfile(IamInstanceProfileSpecification.builder()
                        .name(iamRoleName)
                        .build())
                .build();

        RunInstancesResponse response = ec2Client.runInstances(runRequest);
        String instanceId = response.instances().get(0).instanceId();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(Tag.builder()
                        .key("Name")
                        .value(instanceName)
                        .build())
                .build();

        ec2Client.createTags(tagRequest);



        log.info("Instance with id {} has been started creating", instanceId);
        return instanceId;
    }

    public void waitScriptAndStopInstance(String instanceId) {

        while (true) {
            boolean exists = s3Service.checkFileExists(bucketName, instanceId);
            if (exists) {
                log.info("File exist. Remove file and stop instance");
                s3Service.deleteFile(bucketName, instanceId);
                stopInstance(instanceId);
                log.info("Instance with id {} has been stopped successfully", instanceId);
                break;
            }

            log.info("File does not exist. Wait 3 seconds ...");
            try {
                Thread.sleep(Duration.ofSeconds(3).toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Transactional
    public String getAvailableInstance() {
        List<Instance> readyList = instanceRepo.findReadyInstancesForUpdate();

        // todo
        if (readyList.isEmpty()) {
            return "";
        }

        Instance selected = readyList.get(0);
        selected.setStatus(com.leonid.workerbot.model.InstanceStatus.BUSY_RUNNING);
        instanceRepo.save(selected);

        return selected.getInstanceId();
    }

    public void stopInstance(String instanceId) {
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2Client.stopInstances(request);
    }

    public void sendRequestToStartInstance(String instanceId) {
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2Client.startInstances(request);
    }

    public String getRunningIp(String instanceId) {

        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2Client.startInstances(request);

        boolean isRunning = false;
        String retVal = "";

        while (!isRunning) {
            DescribeInstancesResponse response = ec2Client.describeInstances();

            List<Reservation> reservations = response.reservations();

            for (Reservation reservation : reservations) {
                for (software.amazon.awssdk.services.ec2.model.Instance instance : reservation.instances()) {
                    if (instance.instanceId().equals(instanceId)) {
                        if (instance.state().nameAsString().equals("running")) {
                            retVal = instance.publicIpAddress();
                            isRunning = true;
                        }
                    }
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return retVal;
    }
}
