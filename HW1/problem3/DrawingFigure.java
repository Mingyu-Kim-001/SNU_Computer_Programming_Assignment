public class DrawingFigure {
    public static void drawFigure(int n) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        String[] drawing = new String[6];
        drawing[0] = "////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\";
        drawing[1] = "////////////////********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\";
        drawing[2] = "////////////****************\\\\\\\\\\\\\\\\\\\\\\\\";
        drawing[3] = "////////************************\\\\\\\\\\\\\\\\";
        drawing[4] = "////********************************\\\\\\\\";
        drawing[5] = "****************************************";
        for(int i=0;i<n;i++){
            System.out.println(drawing[i]);
        }
    }
}
