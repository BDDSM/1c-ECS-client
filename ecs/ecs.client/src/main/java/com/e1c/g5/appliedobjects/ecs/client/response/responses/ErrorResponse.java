/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response.responses;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseBase;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseAction;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseCorrelation;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseInformationBase;

/**
 * error {} -- ответ от СВ
 */
public final class ErrorResponse extends EcsResponseBase
{
    public ErrorResponse(ResponseAction action, ResponseCorrelation correlation, Information information)
    {
        super(action, correlation, information);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Information extends ResponseInformationBase
    {
        public static final String CODE_ACCESS_DENIED = "accessDenied";
        public static final String CODE_EXISTS = "exists";
        public static final String SOURCE_AUTH = "auth";

        // Перечисляем ВСЯКИЕ варианты полей, которые могут появиться в данном объекте.
        // Благодаря ignoreUnknown незадействованные будут незадействованы.

        private String code = "";
        private List<String> errors = new ArrayList<>();
        private String recoveryData = "";
        private String recoveryHint = "";
        private String source = "";
        // ...и ещё есть всякое
        // поэтому БЕЗ конструктора, иначе будет ошибка

        public boolean isAccessDenied()
        {
            return CODE_ACCESS_DENIED.equals(code);
        }

        public boolean isCode_exists_And_Source_auth()
        {
            return CODE_EXISTS.equals(code) && SOURCE_AUTH.equals(source);
        }

        @Override
        public String toString()
        {
            return "Information{" +
                    "code='" + code + '\'' +
                    ", errors=" + errors +
                    ", recoveryData='" + recoveryData + '\'' +
                    ", recoveryHint='" + recoveryHint + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        public List<String> getErrors()
        {
            return errors;// != null ? errors : new ArrayList<>();
        }

        public void setErrors(List<String> errors)
        {
            this.errors = errors;
        }

        public String getRecoveryData()
        {
            return recoveryData;
        }

        public void setRecoveryData(String recoveryData)
        {
            this.recoveryData = recoveryData;
        }

        public String getRecoveryHint()
        {
            return recoveryHint;
        }

        public void setRecoveryHint(String recoveryHint)
        {
            this.recoveryHint = recoveryHint;
        }

        public String getSource()
        {
            return source;
        }

        public void setSource(String source)
        {
            this.source = source;
        }
    }

    @Override
    public String toString()
    {
        return "ErrorResponse{" +
                "action=" + action +
                ", correlation=" + correlation +
                ", information=" + information +
                '}';
    }

    public Information getInformation()
    {
        return (Information) information;
    }
}
