package sagan.util.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DateService {
    public Date now() {
        return new Date();
    }
}
