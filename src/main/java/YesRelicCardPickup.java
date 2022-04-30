import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;

@SpirePatch(
	clz= CardRewardScreen.class,
	method="cardSelectUpdate"
)
public class YesRelicCardPickup {

	@SpireInsertPatch(
		loc=289,
		localvars = {"skipButton", "hoveredCard"}
	)
	public static SpireReturn Insert(CardRewardScreen __instance, SkipCardButton skipButton, AbstractCard hoveredCard){
		if(AbstractDungeon.player.hasRelic("YesRelic")){
			System.out.println("Player has YesRelic");

			//if there are cards on the screen, remove the current one
			if(__instance.rItem != null && __instance.rItem.cards != null) {
				//remove the selected card from the options
				__instance.rItem.cards.remove(hoveredCard);

				//as long as any card remains, show the skip button and allow more to be taken
				if (!__instance.rItem.cards.isEmpty()) {
					skipButton.show();

					return SpireReturn.Return(null);
				}
			}
		} else {
			System.out.println("Player does not have YesRelic");
		}
		return SpireReturn.Continue();
	}
}
