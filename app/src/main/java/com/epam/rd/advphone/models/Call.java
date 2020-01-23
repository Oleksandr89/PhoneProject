package com.epam.rd.advphone.models;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Call {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy \nHH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat formatterTime = new SimpleDateFormat("mm:ss", Locale.ENGLISH);

    private int id;
    private String name;
    private final String phone;
    private final String type;
    private final Long date;
    private String photo;
    private String duration;

    private Call(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.phone = builder.phone;
        this.type = builder.type;
        this.date = builder.date;
        this.photo = builder.photo;
        this.duration = builder.duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return formatter.format(new Date(date));
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDuration() {
        long time = Long.parseLong(duration);
        formatterTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatterTime.format(new Date(time * 1000L));
    }

    public static class Builder {
        private int id;
        private String name;
        private String phone;
        private String type;
        private Long date;
        private String photo;
        private String duration;

        public Builder() {
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setDate(Long date) {
            this.date = date;
            return this;
        }

        public Builder setPhoto(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder setDuration(String duration) {
            this.duration = duration;
            return this;
        }

        public Call build() {
            return new Call(this);
        }
    }
}
