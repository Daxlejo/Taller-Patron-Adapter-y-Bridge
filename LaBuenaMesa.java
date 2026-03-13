import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Pedido {

    private String customer;
    private String address;
    private String[] products;
    private double total;
    private String serviceType;

    public Pedido(String customer,String address,String[] products,double total,String serviceType){

        if(customer==null||customer.isEmpty()) throw new IllegalArgumentException("Customer required");
        if(products==null||products.length==0) throw new IllegalArgumentException("Select products");
        if(total<=0) throw new IllegalArgumentException("Total must be > 0");

        this.customer=customer;
        this.address=address;
        this.products=products;
        this.total=total;
        this.serviceType=serviceType;
    }

    public String getCustomer(){return customer;}
    public String getAddress(){return address;}
    public String[] getProducts(){return products;}
    public double getTotal(){return total;}
    public String getServiceType(){return serviceType;}
}

class RappiAPI{
    public String send(String c,String a,String[] p,double tip){
        return "Rappi order for "+c+" | tip $"+String.format("%.2f",tip);
    }
}

class UberAPI{
    public String send(String c,String a,String[] p,String priority){
        return "Uber Eats order for "+c+" | "+priority;
    }
}

class PedidosYaAPI{
    public String send(String c,String a,String[] p,double shipping){
        return "PedidosYa order for "+c+" | shipping $"+String.format("%.2f",shipping);
    }
}

interface Platform{
    String send(Pedido p);
    String name();
}

class RappiAdapter implements Platform{
    RappiAPI api=new RappiAPI();
    public String send(Pedido p){
        return api.send(p.getCustomer(),p.getAddress(),p.getProducts(),p.getTotal()*0.1);
    }
    public String name(){return "Rappi";}
}

class UberAdapter implements Platform{
    UberAPI api=new UberAPI();
    public String send(Pedido p){
        String pr=p.getTotal()>30?"HIGH PRIORITY":"NORMAL";
        return api.send(p.getCustomer(),p.getAddress(),p.getProducts(),pr);
    }
    public String name(){return "Uber Eats";}
}

class PedidosYaAdapter implements Platform{
    PedidosYaAPI api=new PedidosYaAPI();
    public String send(Pedido p){
        double ship=p.getTotal()>20?0:4;
        return api.send(p.getCustomer(),p.getAddress(),p.getProducts(),ship);
    }
    public String name(){return "PedidosYa";}
}

public class LaBuenaMesa extends JFrame{

    JTextField customerField=new JTextField();
    JTextField addressField=new JTextField();
    JTextField totalField=new JTextField();

    JRadioButton dineIn=new JRadioButton("Dine In");
    JRadioButton takeAway=new JRadioButton("Take Away");
    JRadioButton delivery=new JRadioButton("Delivery");

    JCheckBox[] products={
            new JCheckBox("Burger"),
            new JCheckBox("Pizza"),
            new JCheckBox("Tacos"),
            new JCheckBox("Pasta"),
            new JCheckBox("Salad"),
            new JCheckBox("Soup")
    };

    JComboBox<String> platformBox=new JComboBox<>(new String[]{"Rappi","Uber Eats","PedidosYa"});

    JTextArea history=new JTextArea();

    public LaBuenaMesa(){

        Color cream=new Color(250,246,239);
        Color brown=new Color(59,35,20);

        setTitle("La Buena Mesa");
        setSize(1000,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header=new JPanel();
        header.setBackground(brown);
        header.setBorder(new EmptyBorder(15,25,15,25));

        JLabel title=new JLabel("La Buena Mesa");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif",Font.BOLD,26));

        header.add(title);

        add(header,BorderLayout.NORTH);

        JPanel main=new JPanel(new GridLayout(1,2));

        JPanel formPanel=new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel,BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(25,25,25,25));
        formPanel.setBackground(Color.white);

        formPanel.add(label("Customer"));
        formPanel.add(customerField);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(label("Address"));
        formPanel.add(addressField);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(label("Service Type"));

        ButtonGroup group=new ButtonGroup();
        group.add(dineIn);
        group.add(takeAway);
        group.add(delivery);

        JPanel servicePanel=new JPanel(new GridLayout(1,3));
        servicePanel.add(dineIn);
        servicePanel.add(takeAway);
        servicePanel.add(delivery);

        formPanel.add(servicePanel);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(label("Products"));

        JPanel prodPanel=new JPanel(new GridLayout(2,3));
        for(JCheckBox p:products) prodPanel.add(p);

        formPanel.add(prodPanel);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(label("Total"));
        formPanel.add(totalField);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(label("Platform"));
        formPanel.add(platformBox);

        formPanel.add(Box.createVerticalStrut(20));

        JButton sendButton=new JButton("Send Order");
        sendButton.addActionListener(e->sendOrder());

        formPanel.add(sendButton);

        JPanel historyPanel=new JPanel(new BorderLayout());
        historyPanel.setBorder(new EmptyBorder(20,20,20,20));
        historyPanel.setBackground(cream);

        JLabel historyTitle=new JLabel("Order History");
        historyTitle.setFont(new Font("Serif",Font.BOLD,20));

        history.setEditable(false);

        JScrollPane scroll=new JScrollPane(history);

        historyPanel.add(historyTitle,BorderLayout.NORTH);
        historyPanel.add(scroll,BorderLayout.CENTER);

        main.add(formPanel);
        main.add(historyPanel);

        add(main,BorderLayout.CENTER);

        setVisible(true);
    }

    JLabel label(String t){
        JLabel l=new JLabel(t);
        l.setFont(new Font("Arial",Font.BOLD,13));
        return l;
    }

    void sendOrder(){

        try{

            String customer=customerField.getText();
            String address=addressField.getText();
            double total=Double.parseDouble(totalField.getText());

            String service="";
            if(dineIn.isSelected()) service="Dine In";
            if(takeAway.isSelected()) service="Take Away";
            if(delivery.isSelected()) service="Delivery";

            List<String> list=new ArrayList<>();

            for(JCheckBox c:products)
                if(c.isSelected()) list.add(c.getText());

            Pedido pedido=new Pedido(customer,address,list.toArray(new String[0]),total,service);

            Platform p;

            if(platformBox.getSelectedIndex()==0) p=new RappiAdapter();
            else if(platformBox.getSelectedIndex()==1) p=new UberAdapter();
            else p=new PedidosYaAdapter();

            String result=p.send(pedido);

            history.append("\nCustomer: "+pedido.getCustomer());
            history.append("\nService: "+pedido.getServiceType());
            history.append("\nPlatform: "+p.name());
            history.append("\nTotal: $"+pedido.getTotal());
            history.append("\nResult: "+result+"\n");

            clearFields();

        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,ex.getMessage());
        }
    }

    void clearFields(){

        customerField.setText("");
        addressField.setText("");
        totalField.setText("");

        dineIn.setSelected(false);
        takeAway.setSelected(false);
        delivery.setSelected(false);

        for(JCheckBox c:products) c.setSelected(false);

        platformBox.setSelectedIndex(0);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(LaBuenaMesa::new);
    }
}