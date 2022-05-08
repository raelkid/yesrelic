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
import com.megacrit.cardcrawl.relics.AbstractRelic;import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@SpireInitializer
public class ModInitializer implements EditRelicsSubscriber, StartGameSubscriber, EditStringsSubscriber, PostInitializeSubscriber, PostBattleSubscriber {
	//This is for the in-game mod settings panel.
	public static Properties defaultSettings = new Properties();
	public static final String START_WITH_YES_RELIC = "startWithYesRelic";
	public static final String LIMIT_USAGES = "limitUsages";
	public static boolean startWithYesRelic = false;
	public static boolean limitUsages = true;

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
			limitUsages = config.getBool(LIMIT_USAGES);
		} catch(Exception ex){}
	}

	//Used by @SpireInitializer
	public static void initialize(){
		//This creates an instance of our classes and gets our code going after BaseMod and ModTheSpire initialize.
		ModInitializer modInitializer = new ModInitializer();
	}

	@Override
	public void receiveStartGame(){
		if(startWithYesRelic && !AbstractDungeon.player.hasRelic(YesRelic.RELIC_ID)){
			RelicLibrary.getRelic(YesRelic.RELIC_ID).makeCopy().instantObtain(AbstractDungeon.player, 1, false);
		}

		this.resetYesRelicUsage();
	}

	@Override
	public void receiveEditRelics(){
		BaseMod.addRelic(new YesRelic(limitUsages), RelicType.SHARED);
	}

	@Override
	public void receiveEditStrings() {
		//RelicStrings
		String relicStrings = Gdx.files.internal("relicStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
		BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
	}

	@Override
	public void receivePostBattle(AbstractRoom room){
		this.resetYesRelicUsage();
	}

	private void resetYesRelicUsage(){
		if(AbstractDungeon.player.hasRelic(YesRelic.RELIC_ID)){
			((YesRelic)AbstractDungeon.player.getRelic(YesRelic.RELIC_ID)).resetUsage();
		}
	}

	@Override
	public void receivePostInitialize() {
		//Create the Mod Menu
		ModPanel settingsPanel = new ModPanel();

		//Create the toggle to get the relic as a starting relic
		ModLabeledToggleButton startWithRelicCheckbox = new ModLabeledToggleButton(
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

		//Create the checkbox to enable the use limit
		ModLabeledToggleButton limitUsagesCheckbox = new ModLabeledToggleButton(
			"Limit YesRelic to 3 usages (restart required).",
			350.0f, 650.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
			limitUsages,
			settingsPanel,
			(label) -> {},
			(button) -> {
				limitUsages = button.enabled;
				try {
					//store the choice
					SpireConfig config = new SpireConfig("YesRelic", "YesRelicConfig", defaultSettings);
					config.setBool(LIMIT_USAGES, limitUsages);
					config.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		);

		//add the inputs to the screen
		settingsPanel.addUIElement(startWithRelicCheckbox);
		settingsPanel.addUIElement(limitUsagesCheckbox);

		BaseMod.registerModBadge(new Texture("images/badge.png"), MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
	}
}
