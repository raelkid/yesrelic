import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class YesRelic extends AbstractRelic {
	public static final String RELIC_ID = "YesRelic";
	private Boolean limitUsages;

	//these get reset after each combat, this is used so we can measure usages per reward screen
	private boolean pickedCard = false;
	private boolean useRegistered = false;

	public YesRelic(Boolean limitUsages){
		super(RELIC_ID,"yes_relic.png", RelicTier.COMMON, LandingSound.MAGICAL);
		this.limitUsages = limitUsages;
		this.counter = limitUsages ? 3 : -1;
	}

	public void use(){
		//this method is called everytime a card is picked from the reward screen
		//so first time is not actually a usage
		if(!this.pickedCard){
			this.pickedCard = true;
			return;
		}

		//if this is a subsequent click, flash the relic to indicate usage, decrement the counter and update the
		//description in the tooltip.
		this.flash();

		//if we didn't register a use yet, do so know
		if(!this.useRegistered){
			this.useRegistered = true;
			//decrease the counter, if that's applicable
			if(this.counter != -1){
				this.setCounter(this.counter-1);
			}
			this.description = this.getUpdatedDescription();

			//if we're all out of usages, use up the relic (note that additional cards can still be picked up on the
			//current reward screen)
			if(this.counter == 0){
				this.usedUp();
			} else if(this.counter == 1){
				//if this is the final use, change the description
				this.description = this.DESCRIPTIONS[1];
			}
		}
	}

	public void resetUsage(){
		this.pickedCard = false;
		this.useRegistered = false;
	}

	public boolean isUsable(){
		//this relic is usable if the counter allows, or if we've already picked a card while the
		//relic was usable (in which case pickedCard will be true)
		return this.counter == -1 || this.counter > 0 || this.pickedCard;
	}

	@Override
	public AbstractRelic makeCopy(){
		return new YesRelic(this.limitUsages);
	}

	@Override
	public String getUpdatedDescription(){
		if(this.counter == -1){
			return this.DESCRIPTIONS[0].replaceFirst("cnt", "any");
		}
		return this.DESCRIPTIONS[0].replaceFirst("cnt", String.valueOf(this.counter));
	}
}
