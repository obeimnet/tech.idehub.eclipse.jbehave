package tech.idehub.eclipse.jbehave.junit.launcher;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JBehaveJUnitLaunchableTesterTest {

	@Mock
	private IAdaptable iAdaptable;
	
	JBehaveJUnitLaunchableTester tester = spy(new JBehaveJUnitLaunchableTester());
	
	@Test
	public void list_is_not_a_valid_receiver_object() {
		Object receiver = mock(List.class);
		assertFalse(tester.test(receiver, JBehaveJUnitLaunchableTester.PROP_CAN_LAUNCH_JBEHAVE, null, null));
		verify(tester, times(0)).canLaunchJBehave(receiver);
	}
	
}
