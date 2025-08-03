package com.havelsan.api.service;

import com.havelsan.api.exception.InvalidNewsFormatException;
import com.havelsan.api.model.CovidNewsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DataProcessingServiceTest {

    private DataProcessingService service;

    @BeforeEach
    void setUp() {
        service = new DataProcessingService();
    }

    @Test
    void parseValidAnkaraNews() {
        String newsText = "20.04.2020 tarihinde Ankara da Korona virüs salgınında yapılan testlerde 15 yeni vaka bulundu. 1 kişi korona dan vefat etti. 5 kişide taburcu oldu.";

        CovidNewsModel result = service.parseNewsAndFillModel(newsText);

        assertEquals(LocalDate.of(2020, 4, 20), result.getDate());
        assertEquals("Ankara", result.getCity());
        assertEquals(15, result.getCaseCount());
        assertEquals(1, result.getDeathCount());
        assertEquals(5, result.getDischargesCount());
        assertEquals(newsText, result.getTheNews());
    }

    @Test
    void parseIstanbulNewsWithDifferentOrder() {
        String newsText = "Korona virüs salgınında yapılan testlerde 19.04.2020 tarihinde İstanbul da 30 yeni vaka tespit edildi. İstanbul da taburcu sayısı 7 kişi. 3 kişi de vefat etti.";

        CovidNewsModel result = service.parseNewsAndFillModel(newsText);

        assertEquals(LocalDate.of(2020, 4, 19), result.getDate());
        assertEquals("İstanbul", result.getCity());
        assertEquals(30, result.getCaseCount());
        assertEquals(3, result.getDeathCount());
        assertEquals(7, result.getDischargesCount());
    }

    @Test
    void shouldThrowExceptionWhenDateIsMissing() {
        String newsText = "Ankara da Korona virüs salgınında yapılan testlerde 15 yeni vaka bulundu. 1 kişi vefat etti. 5 kişi taburcu oldu.";

        InvalidNewsFormatException exception = assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(newsText));

        assertTrue(exception.getMessage().contains("Date not found"));
    }

    @Test
    void shouldFailWhenCityNotFound() {
        String newsText = "20.04.2020 tarihinde Korona virüs salgınında yapılan testlerde 15 yeni vaka bulundu. 1 kişi vefat etti. 5 kişi taburcu oldu.";

        assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(newsText));
    }

    @Test
    void shouldFailWhenVakaKeywordMissing() {
        String newsText = "20.04.2020 tarihinde Ankara da Korona virüs salgınında yapılan testlerde. 1 kişi vefat etti. 5 kişi taburcu oldu.";

        InvalidNewsFormatException ex = assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(newsText));

        assertTrue(ex.getMessage().contains("Case count (vaka) not found"));
    }

    @Test
    void shouldFailWhenVefatKeywordMissing() {
        String newsText = "20.04.2020 tarihinde Ankara da Korona virüs salgınında yapılan testlerde 15 yeni vaka bulundu. 5 kişi taburcu oldu.";

        assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(newsText));
    }

    @Test
    void shouldFailWhenTaburcuKeywordMissing() {
        String newsText = "20.04.2020 tarihinde Ankara da Korona virüs salgınında yapılan testlerde 15 yeni vaka bulundu. 1 kişi vefat etti.";

        assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(newsText));
    }

    @Test
    void rejectNullInput() {
        assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(null));
    }

    @Test
    void rejectEmptyInput() {
        assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(""));
    }

    @Test
    void parseNewsWithMixedSentenceOrder() {
        String newsText = "15 yeni vaka bulundu. 20.04.2020 tarihinde Ankara da Korona virüs salgınında testler yapıldı. 1 kişi vefat etti. 5 kişi taburcu oldu.";

        CovidNewsModel result = service.parseNewsAndFillModel(newsText);

        assertEquals(LocalDate.of(2020, 4, 20), result.getDate());
        assertEquals("Ankara", result.getCity());
        assertEquals(15, result.getCaseCount());
        assertEquals(1, result.getDeathCount());
        assertEquals(5, result.getDischargesCount());
    }

    @Test
    void parseNewsWithZeroValues() {
        String newsText = "20.04.2020 tarihinde Ankara da Korona virüs salgınında yapılan testlerde 0 yeni vaka bulundu. 0 kişi vefat etti. 0 kişi taburcu oldu.";

        CovidNewsModel result = service.parseNewsAndFillModel(newsText);

        assertEquals(0, result.getCaseCount());
        assertEquals(0, result.getDeathCount());
        assertEquals(0, result.getDischargesCount());
    }

    @Test
    void shouldRejectWrongDateFormat() {
        String newsText = "2020-04-20 tarihinde Ankara da Korona virüs salgınında yapılan testlerde 15 yeni vaka bulundu. 1 kişi vefat etti. 5 kişi taburcu oldu.";

        assertThrows(InvalidNewsFormatException.class,
                () -> service.parseNewsAndFillModel(newsText));
    }
}