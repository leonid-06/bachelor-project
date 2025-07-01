package com.leonid.hosting.bot.service;

import com.leonid.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final TelegramClient client;

    @Value("${faq.url}")
    private String faqUrl;

    public void sendHomePage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("We are glad to see you")
                .replyMarkup(homePageKeyboard())
                .build();
        send(message);
    }

    public void sendDeployHomePage(Long chatId) {
        List<InlineKeyboardButton> buttons = Arrays.stream(ApplicationStack.values())
                .map(stack -> getButton(stack.getLabel(), stack.getValue()))
                .toList();

        int mid = buttons.size() / 2;
        var row1 = new InlineKeyboardRow();
        var row2 = new InlineKeyboardRow();
        row1.addAll(buttons.subList(0, mid));
        row2.addAll(buttons.subList(mid, buttons.size()));


        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Select a stack")
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(row1)
                        .keyboardRow(row2)
                        .build())
                .build();
        send(message);
    }

    // ТУТ Send me by one message в зависимости шо за стек

    public void sendDeployWizard(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Send me by one message")
                .build();
        send(message);
    }

    private ReplyKeyboard homePageKeyboard() {

        InlineKeyboardRow row = new InlineKeyboardRow(
                getButton("Deploy", "/deploy"),
                getButton("FAQ", "/faq"),
                getButton("My apps", "/my-apps"));

        var faq = row.get(1);
        faq.setUrl(faqUrl);

        return InlineKeyboardMarkup.builder()
                .keyboard(Collections.singletonList(row)).build();
    }

    public void sendDeployResult(DeployResponse response) {

        String text = """
                *✅Deployment complete!*
                Your application is now accessible [here](%s)
                """.formatted(response.getIpV4address());

        SendMessage message = SendMessage.builder()
                .chatId(response.getChatId())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
        send(message);
    }

    public void sendDeployResult(ErrorResponse response) {

        SendMessage message = SendMessage.builder()
                .chatId(response.getChatId())
                .text(response.getMessage())
                .build();
        send(message);
    }

    public void notImplementFeature(Long chatId) {

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("We are not yet impl this feature")
                .build();
        send(message);
    }

    private void send(SendMessage message) {
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(EditMessageText message) {
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(DeleteMessage message) {
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private InlineKeyboardButton getButton(String title, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(title)
                .callbackData(callbackData)
                .build();
    }

    private InlineKeyboardRow getRow(List<InlineKeyboardButton> buttons) {
        var row = new InlineKeyboardRow();
        row.addAll(buttons);
        return row;
    }

    public void editToFileRequest(Integer messageId, Long chatId, String filetype) {

        EditMessageText message = EditMessageText.builder()
                .messageId(messageId)
                .chatId(chatId)
                .text("Send your " + filetype + " file")
                .build();
        send(message);

//        EditMessageText editMessage = new EditMessageText();
//        editMessage.setChatId(chatId);
//        editMessage.setMessageId(messageId);
//        editMessage.setText("Отправьте ваш .JAR файл для деплоя:");
//
//        telegramClient.execute(editMessage);

    }

    public void editToWait(Integer messageId, Long chatId) {
        EditMessageText message = EditMessageText.builder()
                .text("Great, we process your request. Wait please")
                .chatId(chatId)
                .messageId(messageId)
                .build();
        send(message);
    }

    public void sendMessage(Long chatId, String s) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(s)
                .build();
        send(message);
    }

    public void sendMessage(Long chatId, String s, boolean isParseMode) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(s)
                .parseMode(ParseMode.MARKDOWN)
                .build();
        send(message);
    }

    public void deleteMessage(Integer messageId, Long chatId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
        send(deleteMessage);
    }

    public void sendUnrecognized(Long chatId) {
        sendMessage(chatId, "Unrecognized message");
    }

    public void sendUserDeployments(Long chatId, List<DeploymentDto> deployments) {
        if (deployments.isEmpty()) {
            sendMessage(chatId, "You don't have any active deployments.");
            return;
        }

        for (DeploymentDto d : deployments) {
            StringBuilder sb = new StringBuilder();
            sb.append("🔹 <b>").append(d.getName()).append("</b>\n");
            sb.append("Stack: ").append(d.getStack()).append("\n");
            sb.append("Status: ").append(d.getStatus()).append("\n");

            if (d.getIpOrUrl() != null && !d.getIpOrUrl().isBlank()) {
                sb.append("See: ").append(d.getIpOrUrl()).append("\n");
            }

            InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                    .keyboard(List.of(

                            getRow(List.of(
                                    getButton("⚙️ Manage", "DEPLOYMENT_MANAGE|instanceId=" + d.getInstanceId() + ";status=" + d.getStatus())
                            ))
                    ))
                    .build();

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(sb.toString())
                    .parseMode("HTML")
                    .replyMarkup(keyboard)
                    .build();

            send(message);
        }
    }

    public void sendDeploymentManagePanel(Long chatId, ApplicationStatus status, String instanceId) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        String baseCallback = "CHANGE_ST|instanceId=" + instanceId;

        if (status == ApplicationStatus.STOPPED) {
            buttons.add(getButton("▶️ Start", baseCallback + ";action=STARTING"));
        } else if (status == ApplicationStatus.RUNNING) {
            buttons.add(getButton("⏹️ Stop", baseCallback + ";action=STOPPED"));
        }
        buttons.add(getButton("❌ Terminate", baseCallback + ";action=TERMINATED"));

        // todo remove later
//        String callback = "DEPLOYMENT_CH;ANGE_STATUSDEPLOY_STfATUS|instanceId=" + instanceId;
//        buttons.add(getButton("❌ Terminate", callback));
//        System.out.println("Callback length: " + callback.getBytes(StandardCharsets.UTF_8).length);


        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboard(List.of(getRow(buttons)))
                .build();

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("🔧 Manage your deployment:")
                .replyMarkup(keyboard)
                .build();

        send(message);
    }

}
