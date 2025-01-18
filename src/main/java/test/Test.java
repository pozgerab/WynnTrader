package test;

import com.gertoxq.wynntrader.custom.AllIDs;
import com.gertoxq.wynntrader.custom.Cast;
import com.gertoxq.wynntrader.custom.CustomItem;
import com.gertoxq.wynntrader.custom.ID;
import com.gertoxq.wynntrader.util.WynnData;
import com.wynntils.models.containers.containers.TradeMarketContainer;
import net.minecraft.text.Text;
import org.lwjgl.system.windows.INPUT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        AllIDs.load();
        WynnData.load();
        System.out.println(CustomItem.bonusRegex.matcher("-5 Charge Cost").matches());
    }
}
