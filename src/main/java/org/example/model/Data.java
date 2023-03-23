package org.example.model;

public abstract class Data {
    private int index;
    private char datatype;
    private int serviceId;
    private int variationId;
    private int questionId;
    private int categoryId;
    private int subCategoryId;
    private char responseType;
    private boolean isValid;

    public Data() {
    }

    public char getDatatype() {
        return datatype;
    }

    public void setDatatype(char datatype) {
        this.datatype = datatype;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getVariationId() {
        return variationId;
    }

    public void setVariationId(int variationId) {
        this.variationId = variationId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public char getResponseType() {
        return responseType;
    }

    public void setResponseType(char responseType) {
        this.responseType = responseType;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
