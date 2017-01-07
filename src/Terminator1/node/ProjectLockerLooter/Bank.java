package Terminator1.node.ProjectLockerLooter;

/**
 * Created by Genoss on 12/25/2016:8:32 AM
 */

import Terminator1.api.Node;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Npc;
import Terminator1.ProjectLockerLooter;

import java.util.concurrent.Callable;

public class Bank extends Node<ClientContext>{

    private ProjectLockerLooter pll;
    private Npc banker;
    public Bank(ClientContext ctx, ProjectLockerLooter mc) {
        super(ctx);
        pll = mc;
    }

    @Override
    public void executeBlock() {
        pll.setStatus("Banking...");
        if(!ctx.bank.opened()) {
            if(!banker.inViewport()) {
                ctx.camera.turnTo(banker);
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return banker.inViewport();
                    }
                });
            }
            ctx.bank.open();
        }
        if(ctx.inventory.select().count() > 0) {
            ctx.bank.depositInventory();
            pll.setLoot(ctx.inventory.select().id(995).poll().stackSize(),ctx.inventory.select().id(pll.sap).count(),ctx.inventory.select().id(pll.eme).count(),ctx.inventory.select().id(pll.rub).count(),ctx.inventory.select().id(pll.dia).count());
        }
        if(ctx.bank.select().id(pll.getFoodID()).count() == 0) {
            if(!ctx.game.tab().name().equals(Game.Tab.LOGOUT))
                ctx.game.tab(Game.Tab.LOGOUT);
            ctx.widgets.widget(182).component(10).click();
            ctx.controller.stop();
        }
        ctx.bank.withdraw(pll.getFoodID(),pll.getFoodAmount());
        ctx.bank.withdraw(pll.getStethID(),1);
    }

    @Override
    public boolean isReady() {
        banker = ctx.npcs.select().id(pll.getBankerID()).nearest().poll();
        return (banker.tile().distanceTo(ctx.players.local().tile())<3||ctx.bank.opened()) && (((ctx.inventory.select().id(pll.getFoodID()).count() == 0)||(!pll.getEatFood()&&ctx.inventory.select().count()==28)) || (ctx.inventory.select().id(pll.getStethID()).count() == 0));
    }
}
