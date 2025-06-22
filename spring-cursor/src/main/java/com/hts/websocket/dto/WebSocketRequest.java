package com.hts.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketRequest {

    @JsonProperty("header")
    private Header header;

    @JsonProperty("body")
    private Body body;

    public WebSocketRequest() {}

    public WebSocketRequest(String approvalKey, String ticker) {
        this.header = new Header(approvalKey);
        this.body = new Body(ticker);
    }

    // Getters and Setters
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    // Inner classes
    public static class Header {
        @JsonProperty("approval_key")
        private String approvalKey;

        @JsonProperty("custtype")
        private String custtype = "P";

        @JsonProperty("tr_type")
        private String trType = "1";

        @JsonProperty("content-type")
        private String contentType = "utf-8";

        public Header() {}

        public Header(String approvalKey) {
            this.approvalKey = approvalKey;
        }

        // Getters and Setters
        public String getApprovalKey() {
            return approvalKey;
        }

        public void setApprovalKey(String approvalKey) {
            this.approvalKey = approvalKey;
        }

        public String getCusttype() {
            return custtype;
        }

        public void setCusttype(String custtype) {
            this.custtype = custtype;
        }

        public String getTrType() {
            return trType;
        }

        public void setTrType(String trType) {
            this.trType = trType;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }

    public static class Body {
        @JsonProperty("input")
        private Input input;

        public Body() {}

        public Body(String ticker) {
            this.input = new Input(ticker);
        }

        // Getters and Setters
        public Input getInput() {
            return input;
        }

        public void setInput(Input input) {
            this.input = input;
        }
    }

    public static class Input {
        @JsonProperty("tr_id")
        private String trId = "H0STCNT0";

        @JsonProperty("tr_key")
        private String trKey;

        public Input() {}

        public Input(String ticker) {
            this.trKey = ticker;
        }

        // Getters and Setters
        public String getTrId() {
            return trId;
        }

        public void setTrId(String trId) {
            this.trId = trId;
        }

        public String getTrKey() {
            return trKey;
        }

        public void setTrKey(String trKey) {
            this.trKey = trKey;
        }
    }
} 