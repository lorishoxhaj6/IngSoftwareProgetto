package test;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import controller.PatientController;
import model.Patient;

class PatientControllerTest {

	@Test
	void testSetUserPatient() {
		PatientController controller = new PatientController();
		Patient u = new Patient("UserMarco","password1",1,2,"Marco","Rossi");
		controller.setUser(u);
	}

	@Test
	void testSetClinic() {
		fail("Not yet implemented");
	}

	@Test
	void testSetAlertService() {
		fail("Not yet implemented");
	}

	@Test
	void testInserisciMisurazione() {
		fail("Not yet implemented");
	}

	@Test
	void testModifyElement() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteMeasurement() {
		fail("Not yet implemented");
	}

	@Test
	void testInsertToggleSymptoms() {
		fail("Not yet implemented");
	}

	@Test
	void testInsertSymptomsOnButtonClick() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteSymptomSelected() {
		fail("Not yet implemented");
	}

	@Test
	void testEnterSymptoms() {
		fail("Not yet implemented");
	}

	@Test
	void testResolveSymptoms() {
		fail("Not yet implemented");
	}

	@Test
	void testPreso() {
		fail("Not yet implemented");
	}

}
