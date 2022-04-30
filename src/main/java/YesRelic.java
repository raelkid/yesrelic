import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class YesRelic extends AbstractRelic {
	public static final String RELIC_ID = "YesRelic";
	public YesRelic() {
		super(RELIC_ID,"yes_relic.png", RelicTier.COMMON, LandingSound.MAGICAL);
	}

	@Override
	public AbstractRelic makeCopy() { // always override this method to return a new instance of your relic
		return new YesRelic();
	}

	@Override
	public String getUpdatedDescription(){
		return "Which card do you want? Yes!";
	}
}
