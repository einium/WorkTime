package ru.einium.worktime;

import org.junit.Assert;
import org.junit.Test;
import ru.einium.worktime.model.WorkTimeModel;

public class WorkTimeModelTests {

    @Test
    public void WorkTimeModelConstructor_CreateNewExemplarWorkTimeModel_CorrectNewState() {
        //Arrange
        //Act
        WorkTimeModel model = new WorkTimeModel();
        //Assert
        Assert.assertEquals(model.isStarted, false);
        Assert.assertEquals(model.isPaused, false);
        Assert.assertEquals(model.getGlobalStartTime(), 0L);
    }
}
