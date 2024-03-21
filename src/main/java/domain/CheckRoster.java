package domain;

public class CheckRoster {
	/**
	 * Array of all linter checks, in the order they should be displayed.
	 */
	public static final Check[] CHECKS = {
		new NamingConventionsCheck(),
		new NoGlobalVariablesCheck(),
		new MethodLengthCheck(),
		new ParameterCountCheck(),
		new UnusedAbstractionsCheck(),
		new InformationHidingCheck(),
		new ProgramToInterfaceNotImplementationCheck(),
		new LowCouplingCheck(),
		new StrategyPatternCheck(),
		new ObserverPatternCheck(),
		new AdapterPatternCheck(),
		new PlantUMLGenerator()
	};
}
