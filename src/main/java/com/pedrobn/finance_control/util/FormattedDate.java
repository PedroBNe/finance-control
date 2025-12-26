package com.pedrobn.finance_control.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormattedDate {
    
    public static String format(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateTime.format(formatter);
        return formattedDate;
    }
}
