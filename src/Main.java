import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Apply Modern "Nimbus" Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Fallback to default if Nimbus fails
        }

        // 2. Start the Application with the Login Screen
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}