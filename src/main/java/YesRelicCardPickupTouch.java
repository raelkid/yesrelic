import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;

@SpirePatch(
	clz= CardRewardScreen.class,
	method="update"
)
public class YesRelicCardPickupTouch {

	@SpireInsertPatch(
		loc=137,
		localvars = {"skipButton", "touchCard"}
	)
	public static SpireReturn Insert(CardRewardScreen __instance, SkipCardButton skipButton, AbstractCard touchCard){
		//if this is not an actual card reward, continue the main method
		if(__instance.rItem == null || __instance.rItem.cards == null){
			return SpireReturn.Continue();
		}

		//if we don't have the relic, or this is a touch screen, continue as well
		if(!AbstractDungeon.player.hasRelic(YesRelic.RELIC_ID) || Settings.isTouchScreen){
			return SpireReturn.Continue();
		}

		//if we do have the relic, but it's all used up, continue as well
		if(!((YesRelic)AbstractDungeon.player.getRelic(YesRelic.RELIC_ID)).isUsable()){
			return SpireReturn.Continue();
		}

		//otherwise, we can just carry on
		//remove the selected card from the options
		__instance.rItem.cards.remove(touchCard);

		//as long as any card remains, show the skip button and allow more to be taken
		if(!__instance.rItem.cards.isEmpty()){
			skipButton.show();

			//call the use method on our the YesRelic (this can be called on any card draw)
			((YesRelic)AbstractDungeon.player.getRelic(YesRelic.RELIC_ID)).use();

			return SpireReturn.Return(null);
		}
		return SpireReturn.Continue();
	}
}
