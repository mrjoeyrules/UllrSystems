package Reports;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class ReportTest {

    @Test
    public void testReportGettersAndSetters() {
        Report report = new Report();
        report.SetEventId(999);
        report.SetEventType(1);
        report.SetEventText("Test event text");
        LocalDateTime now = LocalDateTime.now();
        report.SetEventTime(now);

        assertEquals("Event ID should be 999.", 999, report.GetEventId());
        assertEquals("Event Type should be 1.", 1, report.GetEventType());
        assertEquals("Event Text should be 'Test event text'.", "Test event text", report.GetEventText());
        assertEquals("Event Time should match 'now'.", now, report.GetEventTime());
    }
}
