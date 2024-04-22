package domain;

import domain.checks.AdapterPatternCheck;
import domain.checks.ConstantInterfaceCheck;
import domain.checks.ImmutableExceptionsCheck;
import domain.checks.InformationHidingCheck;
import domain.checks.LowCouplingCheck;
import domain.checks.MethodLengthCheck;
import domain.checks.NamingConventionsCheck;
import domain.checks.NoGlobalVariablesCheck;
import domain.checks.ObserverPatternCheck;
import domain.checks.ParameterCountCheck;
import domain.checks.PlantUMLGenerator;
import domain.checks.ProgramToInterfaceNotImplementationCheck;
import domain.checks.RequiredOverridesCheck;
import domain.checks.StrategyPatternCheck;
import domain.checks.UnusedAbstractionsCheck;

public final class CheckRoster {
	/**
	 * Array of all linter checks, in the order they should be displayed.
	 */
	public static final Check[] CHECKS = {
		new NamingConventionsCheck(),
		new MethodLengthCheck(),
		new ParameterCountCheck(),
		new NoGlobalVariablesCheck(),
		new RequiredOverridesCheck(),
		new UnusedAbstractionsCheck(),
		new ImmutableExceptionsCheck(),
		new InformationHidingCheck(),
		new ProgramToInterfaceNotImplementationCheck(),
		new LowCouplingCheck(),
		new StrategyPatternCheck(),
		new ObserverPatternCheck(),
		new AdapterPatternCheck(),
		new ConstantInterfaceCheck(),

		new PlantUMLGenerator()
	};
}
