package com.e1c.g5.appliedobjects.ecs.client.response;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;

import com.e1c.g5.appliedobjects.ecs.client.IMessagesList;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiProtocolException;

public abstract class EcsResponseFactory implements IEcsResponseFactory
{
    /**
     * Подобъекты ответа протокола СВ (см. {@link EcsResponseParser}); создаются в ходе разбора строки сообщения.
     */
    protected ResponseAction action;                // третий элемент протокола - "действие"
    protected ResponseCorrelation correlation;      // второй элемент протокола - "связка"
    protected ResponseInformationBase information;  // первый элемент протокола - "информация"

    /**
     * ФАБРИКА : должна создавать объект ответа из строки сообщения.
     *
     * @param json строка сообщения
     * @return объект ответа
     */
    public abstract EcsResponseBase createResponse(String json);

    /**
     * Выделяет из сообщения протокола все 3 (или 2 из 3-х) части (см. {@link EcsResponseParser})
     * и создает из них соответствующие подобъекты-части (для объекта ответа), согласно классам, указанным в параметрах.
     * <br><br>
     * Третья часть в строке запроса может содержать НЕ объект, а неспецифицированный МАССИВ.<br>
     * В этом случае (т.к. он НЕ обёрнут в объект) он НЕ хочет парситься в подобъект типа {@link ResponseInformationBase}
     * (объект, содержащий внутри себя List c элементами _специфического_ типа), потому что это НЕ объект, а МАССИВ.<br>
     * Поэтому мапим его сначала в List c элементами _специфического_ типа (специфицированным reader'ом),
     * и затем передаем его в конструктор-принимающий-коллекцию для создания 3-го подобъекта
     * (у него ДОЛЖЕН быть определен такой конструктор).
     *
     * @param json                        JSON-строка сообщения (см. {@link EcsResponseParser})
     * @param actionClass                 .class 1-го подобъекта ответа
     * @param correlationClass            .class 2-го подобъекта ответа
     * @param informationClass            .class 3-го подобъекта ответа
     * @param specificArrayElementsReader специфицированный преобразователь элементов массива нужного типа (или null)
     */
    public void parseMessageToResponseSubobjects(String json,
                                                 Class<? extends ResponseAction> actionClass,
                                                 Class<? extends ResponseCorrelation> correlationClass,
                                                 Class<? extends ResponseInformationBase> informationClass,
                                                 ObjectReader specificArrayElementsReader)
    {
        try
        {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            JsonNode jnAction = root.get(0);
            JsonNode jnCorrelation = root.get(1);
            JsonNode jnInformation = root.get(2);

            // Распознаем и создаем все составляющие ответа

            action = json2object(jnAction, actionClass);
            correlation = json2object(jnCorrelation, correlationClass);

            if (specificArrayElementsReader == null)
            {
                // 3-й подобъект создаем обычным методом
                information = json2object(jnInformation, informationClass);
                if (information == null)
                {   // 3-го подобъекта в JSON-ответе может и НЕ быть - создаем "пустой"
                    information = informationClass.getDeclaredConstructor().newInstance();
                }
            }
            else
            {
                // 3-й подобъект создаем специфическим методом - на основе массива, выделенного из 3-й части ответа
                List<Object> specificArrayElements = specificArrayElementsReader.readValue(jnInformation);
                information = informationClass.getDeclaredConstructor(List.class).newInstance(specificArrayElements);
            }
        }
        catch (IOException e)
        {   // readTree() =>
            throw new EcsApiProtocolException(
                    new IllegalStateException(
                            IMessagesList.Messages
                                    .protocol__cant_transform_JSON_string_to_response_object(), e));
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException e)
        {   // getDeclaredConstructor(), newInstance() =>
            throw new EcsApiProtocolException(
                    new IllegalArgumentException(
                            IMessagesList.Messages
                                    .parser__cant_construct_specified_response_object(), e));
        }
        catch (Exception e)
        {
            throw new EcsApiProtocolException(
                    new IllegalStateException(
                            IMessagesList.Messages
                                    .protocol__unknown_JSON_string_parse_error(), e));
        }

        // Должны быть распознаны и созданы все 3 составляющие ответа

        if (action == null || correlation == null)
        {
            throw new EcsApiProtocolException(
                    new IllegalStateException(
                            IMessagesList.Messages
                                    .protocol__error_that_should_not_be()));
        }
    }
}
