package GUI;

public interface GuiControllerFactory {
    GuiControllerFactory FACTORY = new GuiController();

    void startLoop();
}
