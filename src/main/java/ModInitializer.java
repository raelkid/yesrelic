import basemod.*;
import basemod.abstracts.CustomRelic;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@SpireInitializer
public class ModInitializer implements EditRelicsSubscriber, StartGameSubscriber, EditStringsSubscriber, PostInitializeSubscriber {
	//This is for the in-game mod settings panel.
	public static Properties defaultSettings = new Properties();
	public static final String START_WITH_YES_RELIC = "startWithYesRelic";
	public static boolean startWithYesRelic = false;

	private static final String MODNAME = "YesRelic";
	private static final String AUTHOR = "rael_kid";
	private static final String DESCRIPTION = "A relic that allows you to pick multiple cards from combat card rewards.";

	public ModInitializer(){
		//Use this for when you subscribe to any hooks offered by BaseMod.
		BaseMod.subscribe(this);

		try {
			SpireConfig config = new SpireConfig("YesRelic", "YesRelicConfig", defaultSettings);
			config.load();
			startWithYesRelic = config.getBool(START_WITH_YES_RELIC);
		} catch(Exception ex){}
	}

	//Used by @SpireInitializer
	public static void initialize(){
		//This creates an instance of our classes and gets our code going after BaseMod and ModTheSpire initialize.
		ModInitializer modInitializer = new ModInitializer();
	}

	@Override
	public void receiveStartGame(){
		if(startWithYesRelic && !AbstractDungeon.firstRoomChosen){
			RelicLibrary.getRelic("YesRelic").makeCopy().instantObtain(AbstractDungeon.player, 1, false);
		}
	}

	@Override
	public void receiveEditRelics(){
		BaseMod.addRelic(new YesRelic(), RelicType.SHARED);
	}

	@Override
	public void receiveEditStrings() {
		//RelicStrings
		String relicStrings = Gdx.files.internal("relicStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
		BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
	}

	@Override
	public void receivePostInitialize() {
		//Create the Mod Menu
		ModPanel settingsPanel = new ModPanel();

		//Create the toggle to get the relic as a starting relic
		ModLabeledToggleButton startWithRelicButton = new ModLabeledToggleButton(
			"Get YesRelic as an additional starting relic.",
			350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
			startWithYesRelic,
			settingsPanel,
			(label) -> {},
			(button) -> {
				startWithYesRelic = button.enabled;
				try {
					//store the choice
					SpireConfig config = new SpireConfig("YesRelic", "YesRelicConfig", defaultSettings);
					config.setBool(START_WITH_YES_RELIC, startWithYesRelic);
					config.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);

		settingsPanel.addUIElement(startWithRelicButton); // Add the button to the settings panel.

		BaseMod.registerModBadge(new Texture("images/badge.png"), MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
	}
}
