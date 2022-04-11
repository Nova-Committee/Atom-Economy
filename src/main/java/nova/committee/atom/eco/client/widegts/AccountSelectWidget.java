package nova.committee.atom.eco.client.widegts;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import nova.committee.atom.eco.core.model.UseAccount;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/11 18:50
 * Version: 1.0
 */
public class AccountSelectWidget extends AbstractWidget {

    private final int rows;
    private final AccountButton.Size size;
    private final Supplier<List<UseAccount>> accSupplier;
    private final Supplier<UseAccount> selectedAcc;
    private final Consumer<Integer> onPress;
    private List<AccountButton> accButtons = Lists.newArrayList();
    private int scroll = 0;

    public AccountSelectWidget(int x, int y, int rows, Supplier<List<UseAccount>> teamSource, Supplier<UseAccount> selectedTeam, Consumer<Integer> onPress) {
        this(x, y, rows, AccountButton.Size.WIDE, teamSource, selectedTeam, onPress);
    }

    public AccountSelectWidget(int x, int y, int rows, AccountButton.Size size, Supplier<List<UseAccount>> aSource, Supplier<UseAccount> selectedA, Consumer<Integer> onPress) {
        super(x, y, size.width, AccountButton.HEIGHT * rows, new TextComponent(""));
        this.rows = rows;
        this.size = size;
        this.accSupplier = aSource;
        this.selectedAcc = selectedA;
        this.onPress = onPress;
    }

    public void init(Consumer<Button> addButton, Font font) {
        for (int i = 0; i < this.rows; ++i) {
            int index = i;
            AccountButton button = new AccountButton(this.x, this.y + i * AccountButton.HEIGHT, this.size, this::onTeamSelect, font, () -> this.getAcc(index), () -> this.isSelected(index));
            this.accButtons.add(button);
            addButton.accept(button);
        }
    }

    private UseAccount getAcc(int index) {
        List<UseAccount> teamList = accSupplier.get();
        this.validateScroll(teamList.size());
        index += this.scroll;
        if (index >= 0 && index < teamList.size())
            return teamList.get(index);
        return null;
    }

    private boolean isSelected(int index) {
        UseAccount team = getAcc(index);
        if (team == null)
            return false;
        return team == this.selectedAcc.get();
    }

    private void validateScroll(int teamListSize) {
        this.scroll = Mth.clamp(scroll, 0, this.maxScroll(teamListSize));
    }

    private int maxScroll(int teamListSize) {
        return Mth.clamp(teamListSize - this.rows, 0, Integer.MAX_VALUE);
    }

    private boolean canScrollDown() {
        return scroll < this.maxScroll(this.accSupplier.get().size());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!this.visible)
            return false;

        if (delta < 0) {
            if (this.canScrollDown())
                scroll++;
            else
                return false;
        } else if (delta > 0) {
            if (scroll > 0)
                scroll--;
            else
                return false;
        }

        return true;
    }

    private void onTeamSelect(Button button) {
        int index = this.accButtons.indexOf(button);
        if (index < 0)
            return;
        this.onPress.accept(this.scroll + index);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrator) {
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}
