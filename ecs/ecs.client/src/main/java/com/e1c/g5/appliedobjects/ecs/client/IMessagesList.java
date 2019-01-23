package com.e1c.g5.appliedobjects.ecs.client;

import com._1c.g5.i18n.DefaultString;
import com._1c.g5.i18n.Localizable;
import com._1c.g5.i18n.LocalizableFactory;

@Localizable
public interface IMessagesList
{
    @DefaultString("Ошибка протокола: НЕ могу преобразовать объект запроса в JSON-строку")
    String protocol__cant_transform_request_object_to_JSON_string();

    @DefaultString("Ошибка протокола: НЕ могу преобразовать JSON-строку в объекты ответа")
    String protocol__cant_transform_JSON_string_to_response_object();

    @DefaultString("Ошибка анализатора протокола: НЕ могу вызвать конструктор указанного класса")
    String parser__cant_construct_specified_response_object();

    @DefaultString("Ошибка протокола: неведомая ошибка при анализе JSON-строки ответа")
    String protocol__unknown_JSON_string_parse_error();

    @DefaultString("Ошибка анализатора протокола: недопустимое состояние -- объекты НЕ распознаны - что-то пошло не так...")
    String protocol__error_that_should_not_be();

    @DefaultString("Ошибка протокола: НЕизвестный тип ответа")
    String protocol__unknown_response_action_type();

    IMessagesList Messages = LocalizableFactory.create(IMessagesList.class);
}
