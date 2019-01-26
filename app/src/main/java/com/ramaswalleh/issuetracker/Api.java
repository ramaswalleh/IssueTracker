package com.ramaswalleh.issuetracker;

public class Api {
    private static final String ROOT_URL = "http://192.168.43.216/api/v1/Api.php?apicall=";
    public static final String URL_CREATE_ISSUE = ROOT_URL + "createissue";
    public static final String URL_READ_ISSUE = ROOT_URL + "getissue";
    public static final String URL_UPDATE_ISSUE = ROOT_URL + "updateissue";
    public static final String URL_DELETE_ISSUE = ROOT_URL + "deleteissue&id=";
}