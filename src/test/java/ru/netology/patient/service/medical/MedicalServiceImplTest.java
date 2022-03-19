package ru.netology.patient.service.medical;

import org.junit.jupiter.api.*;

import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;


class MedicalServiceImplTest {

    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream systemOut = System.out;
    private MedicalServiceImpl medicalService;
    private String patientId = "123";

    @BeforeEach
    public void medicalServiceInit() {
        System.setOut(new PrintStream(output));

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(Mockito.anyString()))
                .thenReturn(new PatientInfo("123", "Иван", "Петров", LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("38.65"), new BloodPressure(130, 90))));

        SendAlertService alertService = Mockito.spy(SendAlertServiceImpl.class);

        medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
    }

    @Test
    void checkBloodPressureShouldOutput() {
        BloodPressure bloodPressure = new BloodPressure(120, 80);
        medicalService.checkBloodPressure(patientId, bloodPressure);

        String expected = String.format("Warning, patient with id: %s, need help%n", patientId);
        String actual = output.toString();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkTemperatureShouldOutput() {
        BigDecimal temperature = BigDecimal.valueOf(36.6);
        medicalService.checkTemperature(patientId, temperature);

        String expected = String.format("Warning, patient with id: %s, need help%n", patientId);
        String actual = output.toString();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void checkTemperatureShouldNotOutput(){
        BigDecimal temperature = BigDecimal.valueOf(38.6);
        medicalService.checkTemperature(patientId, temperature);

        String expected = "";
        String actual = output.toString();
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void checkBloodPressureShouldNotOutputWithNormal(){
        BloodPressure bloodPressure = new BloodPressure(130, 90);
        medicalService.checkBloodPressure(patientId, bloodPressure);

        String expected = "";
        String actual = output.toString();
        Assertions.assertEquals(expected,actual);
    }


    @AfterEach
    public void cleanUpStreams() {
        System.setOut(systemOut);
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}