package es.grise.upm.profundizacion.mocking.exercise2;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.DoubleStream;

public class EngineControllerTest {

    @Mock private Logger logger;

    @Mock private Speedometer speedometer;

    @Mock private Gearbox gearbox;

    @Mock private Time time;

    @InjectMocks private EngineController engineController;

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis()); 

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
       
        when(time.getCurrentTime()).thenReturn(timestamp);
    }
    @Test @DisplayName(value = "Test 1: El mensaje de log tiene el formato correcto")
    public void testRecordGearLogFormat() {
        GearValues newGear = GearValues.FIRST;

        engineController.recordGear(newGear);
        String expectedLogMessage = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp.getTime())) + " Gear changed to FIRST";
        verify(logger).log(expectedLogMessage);
    }
    @Test @DisplayName(value = "Test 2: Se calcula correctamente la velocidad instantánea")
    public void testCorrectSpeedCalculation() {
        double slow = 10.0, med = 20.0, fast = 30.0;

        when(speedometer.getSpeed()).thenReturn(slow, med, fast);
        
        DoubleStream speeds = DoubleStream.of(slow, med, fast);
        assertEquals(engineController.getInstantaneousSpeed(), speeds.average().getAsDouble());
    }
    @Test @DisplayName(value = "Test 3: El método adjustGear invoca exactamente tres veces al método getSpeed()")
    public void testAdjustGearCallsGetInstantaneousSpeedThreeTimes() {

        engineController.adjustGear();

        verify(speedometer, times(3)).getSpeed();
    }
    @Test @DisplayName(value = "Test 4: El método adjustGear loguea la nueva marcha")
    public void testAdjustGearLogsNewGear() {

        engineController.adjustGear();

        verify(logger).log(anyString());  
    }
    @Test @DisplayName(value = "Test 5: El método adjustGear asigna correctamente la nueva marcha")
    public void testSetGear() {//T
        doNothing().when(gearbox).setGear(GearValues.FIRST);
        when(speedometer.getSpeed()).thenReturn(1.0, 2.0, 6.0);

        engineController.adjustGear();
        verify(gearbox, times(1)).setGear(GearValues.FIRST);
    }
}
