package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new sd170145_AddressOperations();
        CityOperations cityOperations = new sd170145_CityOperations();
        CourierOperations courierOperations = new sd170145_CourierOperations();
        CourierRequestOperation courierRequestOperation = new sd170145_CourierRequestOperations();
        DriveOperation driveOperation = new sd170145_DriveOperations();
        GeneralOperations generalOperations = new sd170145_GeneralOperations();
        PackageOperations packageOperations =  new sd170145_PackageOperations();
        StockroomOperations stockroomOperations = new sd170145_StockroomOperations();
        UserOperations userOperations = new sd170145_UserOperations();
        VehicleOperations vehicleOperations = new sd170145_VehicleOperations();

        
generalOperations.eraseAll();
TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();
    }
        
        
}
