package com.shiguang.lostfound.storage;

public record StoredImage(String key, String url, String contentType, long size) {}
