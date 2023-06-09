package ru.example.api;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.example.jpa.repositories.MessageRepository;
import ru.example.model.Answer;
import ru.example.model.dto.MessageDto;
import ru.example.model.dto.MessageType;
import ru.example.sevices.IMessageSaver;

@Service
@AllArgsConstructor
public class TelegramBotUpdateHandlerImpl implements TelegramBotUpdateHandler {

    private final IMessageSaver messageSaver;

    @Override
    public TelegramBotUpdateResponse send(TelegramBotUpdateRequest request) {
        MessageDto messageDto = request.getUpdate().getMessage();
        messageSaver.save(messageDto);
        return TelegramBotUpdateResponse.builder().answer(fillAnswer(Answer.builder(), messageDto)).build();
    }

    private Answer fillAnswer(Answer.AnswerBuilder builder, MessageDto messageDto) {
        if (messageDto.getMessageType() == MessageType.VIDEO) {
            return builder.text("Хуя, это видео")
                    .chatId(messageDto.getChat().getId())
                    .build();
        }
        if (messageDto.getMessageType() == MessageType.PHOTO) {
            return builder.text("Хуя, это картинка")
                    .chatId(messageDto.getChat().getId())
                    .build();
        }
        return getTextAnswer(messageDto);
    }

    private Answer getTextAnswer(MessageDto message) {
        String text = message.getText();
        Answer.AnswerBuilder answerBuilder = Answer.builder().chatId(message.getChat().getId());
        switch (text) {
            case "/start":
                return answerBuilder.text("Hi, " + message.getFrom().getUsername() + ", nice to meet you!").build();
            case ".":
                return answerBuilder.text(",").build();
            case ",":
                return answerBuilder.text("").build();
        }
        return answerBuilder.build();
    }
}
