package org.example.server;

import java.util.Objects;

public class BackResponse {
    private String response;
    private String value;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackResponse that = (BackResponse) o;
        return Objects.equals(response, that.response) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(response, value);
    }
}
