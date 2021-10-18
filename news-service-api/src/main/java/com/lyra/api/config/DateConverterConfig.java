package com.lyra.api.config;


import com.lyra.exception.GraceException;
import com.lyra.result.ResponseStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 请求路径url中的参数进行时间日期类型的转换，字符串->日期Date
 */
@Configuration
public class DateConverterConfig implements Converter<String, LocalDate> {

    private static final List<String> formatterList = new ArrayList<>(4);
    static{
        formatterList.add("yyyy-MM");
        formatterList.add("yyyy-MM-dd");
        formatterList.add("yyyy-MM-dd hh:mm");
        formatterList.add("yyyy-MM-dd hh:mm:ss");
    }

    @Override
    public LocalDate convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }

        if(source.matches("^\\d{4}-\\d{1,2}$")){
            return parseDate(source, formatterList.get(0));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")){
            return parseDate(source, formatterList.get(1));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formatterList.get(2));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formatterList.get(3));
        }else {
            GraceException.display(ResponseStatusEnum.SYSTEM_DATE_PARSER_ERROR);
        }

        return null;
    }

    /**
     * 日期转换方法
     * @param dateStr
     * @param formatter
     * @return
     */
    public LocalDate parseDate(String dateStr, String formatter) {
        LocalDate date=null;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(formatter));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
