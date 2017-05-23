package net.xalcon.analyzeio;

import net.minecraftforge.common.config.Config;

@Config(modid = AnalyzeIO.MODID)
public class AnalyzeIOConfig
{
	@Config.Comment("0 = disable tooltip; 1 = Show values in tooltip; 2 = Show values in tooltip if capacitor has been analyzed")
	@Config.RangeInt(min = 0, max = 2)
	public static int tooltip = 2;

	@Config.Comment("Analyzer power configuration")
	public static PowerRequirements analyzerPower;

	public static class PowerRequirements
	{
		@Config.Comment("If true, this object requires power to work")
		public static boolean requiresPower = true;

		@Config.Comment("The amount of power needed per tick")
		public static int powerPerTick = 1;

		@Config.Comment("The amount of power this object can hold")
		public static int powerBuffer = 32000;

		@Config.Comment("The amount of power this object can receive per tick")
		public static int powerIntake = 512;
	}
}
