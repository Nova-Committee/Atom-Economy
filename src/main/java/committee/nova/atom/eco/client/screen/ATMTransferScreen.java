package committee.nova.atom.eco.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import committee.nova.atom.eco.Eco;
import committee.nova.atom.eco.client.widegts.AccountList;
import committee.nova.atom.eco.common.containers.ATMContainer;
import committee.nova.atom.eco.utils.UserUtil;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.UUID;
import java.util.function.Consumer;


/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/5 11:34
 * Version: 1.0
 */
public class ATMTransferScreen extends AbstractScreenPage<ATMContainer>{

    AccountList.AccountEntry defaultAccountEntry;
    String lastTickSearch = null;
    private  AccountList accountList;
    private  TextFieldWidget search;
    private  TextFieldWidget values;
    private  ExtendedButton transfer;
    private String to_id;


    protected ATMTransferScreen(ATMContainer container, ContainerScreen<ATMContainer> parent, Consumer<Integer> changePage) {
        super(container, parent, changePage);
    }

    @Override
    public void init() {
        final int halfW = width / 2;
        final int halfH = height / 2;

        accountList = new AccountList(minecraft, 100,200,parent.getGuiTop() + 24,parent.getGuiTop() + parent.getYSize() - 24, 16);
        accountList.setLeftPos(parent.getGuiLeft() + 10);
        defaultAccountEntry = new AccountList.AccountEntry("player", new UUID(0, 0).toString(),  accountList, t -> transfer.active = false);

        search = new TextFieldWidget(font, parent.getGuiLeft() + 10, parent.getGuiTop() + 8,  100, 16, new TranslationTextComponent("gui.atomeco.transfer.search"));

        values = new TextFieldWidget(font, parent.getGuiLeft() + 10, parent.getGuiTop() + parent.getYSize() - 24,  100, 16, new TranslationTextComponent("gui.atomeco.transfer.transfer_value"));

        transfer = new ExtendedButton(halfW + 40,parent.getGuiTop()+parent.getYSize() - 60,60,20,new TranslationTextComponent("gui.atomeco.index.transfer"),
                t-> transferAct());

        accountList.children().add(defaultAccountEntry);

        accountList.setSelected(defaultAccountEntry);

        values.setSuggestion(I18n.get("gui.atomeco.transfer.transfer_value"));
        search.setSuggestion(I18n.get("gui.atomeco.transfer.search"));

        transfer.active = false;//未选定账户前无法激活



        this.addButton(new ExtendedButton(halfW + 40, parent.getGuiTop()+parent.getYSize() - 30, 60, 20, new TranslationTextComponent("gui.atomeco.back"),
                $ -> NavigateTo(0)
        ));

        this.buttons.add(transfer);

        this.children.add(accountList);
        this.children.add(values);
        this.children.add(search);
        this.children.add(transfer);

    }

    @Override
    public void renderInternal(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {

        accountList.render(matrix, mouseX, mouseY, partialTicks);
        search.render(matrix, mouseX, mouseY, partialTicks);
        values.render(matrix, mouseX, mouseY, partialTicks);
        transfer.render(matrix, mouseX, mouseY, partialTicks);

        String title = I18n.get("gui.atomeco.transfer.all_account");
        if(accountList.getSelected() != null) title = accountList.getSelected().getName();
        matrix.pushPose();
        matrix.scale(1.3f,1.3f,1.3f);
        font.draw(matrix, new TranslationTextComponent("gui.atomeco.transfer.tip"), (int)((this.parent.getGuiLeft() + 130) / 1.3),parent.getGuiTop() + 4,0xFFFFF0);
        font.draw( matrix, title, (int)((this.parent.getGuiLeft()+130) / 1.3),parent.getGuiTop() + 14,0xFFFFF0);
        matrix.popPose();
    }

    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY) {

    }

    @Override
    public void tick() {
        if(!search.getValue().equals(lastTickSearch))
        {
            accountList.children().clear();

            accountList.children().add(defaultAccountEntry);


            container.searchAccountAction("player", UserUtil.getUUIDByName(search.getValue()).toString()).forEach(
                    t-> accountList.children().add( new AccountList.AccountEntry("player", t.getId(),  accountList, this::onAccountEntrySelected))
            );

            lastTickSearch = search.getValue();
        }
        else if(search.getValue().equals(null)){

            accountList.children().clear();

            accountList.children().add(defaultAccountEntry);


            container.gatherAccountAction().forEach(
                    t-> accountList.children().add( new AccountList.AccountEntry("player", t.getId(),  accountList, this::onAccountEntrySelected))
            );

            lastTickSearch = search.getValue();
        }
    }

    private void transferAct(){
        long money;
        try {
           money  = Long.parseLong(values.getValue()) * 1000;
            if(container.processOthersAction(to_id, money)){
                container.player.closeContainer();
                Eco.LOGGER.info("transfer success");
            }
        }
        catch (Exception e){
           container.player.sendMessage(new StringTextComponent("不是有效的数字"), UUID.randomUUID());
        }

    }

    private void onAccountEntrySelected(AccountList.AccountEntry entry)
    {

        transfer.active = true;
        this.to_id = entry.getId();
        container.player.sendMessage(new StringTextComponent(to_id), UUID.randomUUID());

    }
}
