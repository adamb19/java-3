/**
 *  MainMenu class for Bigg City Bank system
 *  
 *  @author w15009283/Adam Baker
 *  
 *  @version version 1 9/11/2016
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;



public class MainMenu extends JFrame implements ActionListener {
  // data collections
  private Map<Integer, Client> clients;
  private Map<String, CurrentAccount> accounts;
  private List<Transaction> pendingTransactions, completedTransactions;
  
  //GUI
  private TransactionDlg transactionDlg;
  private JButton btnLoadData,
                  btnCredit, btnDebit, btnProcessTransactions,
                  btnListClients, btnListAccounts,btnCompleteTransactions,btnPendingTransactions,
                  btnSaveData;

  /**
   * this static void will make a main menu app where you 
   * will able to press the buttons and this will visible
   * 
   * @param string[] args
   */
  public static void main(String[] args) {
    MainMenu app = new MainMenu();
    app.setVisible(true);
  }

  /**
   * this constrcutor will has all the buttons
   * and layout configurations as well the lists, and hash maps.
   */
  public MainMenu() {
    // Database
    clients = new HashMap<Integer, Client>();
    accounts = new HashMap<String, CurrentAccount>();
    pendingTransactions = new LinkedList<Transaction>();
    completedTransactions = new LinkedList<Transaction>();

    // GUI - create custom dialog instances
    transactionDlg = new TransactionDlg(this);

    // GUI - set window properties
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(200, 100, 250, 300);

    //GUI - main menu buttons    
    JPanel mainPnl = new JPanel();
    mainPnl.setLayout(new GridLayout(8,1));

    btnLoadData = new JButton("Load Data");
    btnLoadData.addActionListener(this);
    mainPnl.add(btnLoadData);
    
    btnCredit = new JButton("Credit");
    btnCredit.addActionListener(this);
    mainPnl.add(btnCredit);
    
    btnDebit = new JButton("Debit");
    btnDebit.addActionListener(this);
    mainPnl.add(btnDebit);
    
    btnProcessTransactions = new JButton("Process Transactions");
    btnProcessTransactions.addActionListener(this);
    mainPnl.add(btnProcessTransactions);
 
    btnListClients = new JButton("List Clients");
    btnListClients.addActionListener(this);
    mainPnl.add(btnListClients);
    
    btnListAccounts = new JButton("List Accounts");
    btnListAccounts.addActionListener(this);
    mainPnl.add(btnListAccounts);
    
    btnCompleteTransactions= new JButton("Complete transaction");
    btnCompleteTransactions.addActionListener(this);
    mainPnl.add(btnCompleteTransactions);
    
    btnPendingTransactions= new JButton("Pending Tranctions");
    btnPendingTransactions.addActionListener(this);
    mainPnl.add(btnPendingTransactions);
    
    btnSaveData= new JButton("save");
    btnSaveData.addActionListener(this);
    mainPnl.add(btnSaveData);
   
    add(mainPnl, BorderLayout.CENTER);
  } //end constructor

  //Accessors for data structures
  public Map<Integer, Client>  getClients()         { return clients;      }   
  public Map<String, CurrentAccount> getAccounts()  { return accounts;     }
  public List<Transaction> getPendingTransactions() {
    return pendingTransactions;
  }
  public List<Transaction> getCompletedTransactions() {
    return completedTransactions;
  }

  /**
   * Actions in response to button clicks
   */
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    //read customers, items, orders JUST ONCE to initialise the system data.
    if (src == btnLoadData) { 
      loadClientData();
      loadAccountData();
      loadPendingTransactions();
      btnLoadData.setEnabled(false);  
    }
    else if (src == btnCredit) { // dialog will do multiple credit transactions
      transactionDlg.setCreditMode();
      transactionDlg.setVisible(true);
    }
    else if (src == btnDebit) { // dialog will do multiple debit transactions
      transactionDlg.setDebitMode();
      transactionDlg.setVisible(true);
    }
   else if (src == btnProcessTransactions) { // iterate through order
       /**
        * I have mad eall the variable frst
        * after this I started to iterate through the pending transaction by iusing a while loop
        * after this I also had a for loop so it will continue to go through all the transaction
        * until it reached the end
        * the id in the transaction will equal to the account id and amount will equal to the amount
        * 
        * if account in the currentaccount was null a mmesssage will pop up.
        * if it isn't it will call the debit function
        * after this if eveerything was correct it remove the record from pending 
        * put in completed, change accounts details.
        */
       String acc = "";
        int amt = 0;
        Iterator<Transaction> i = pendingTransactions.iterator();
        while (i.hasNext()) {//iterATE THROUGH
            for (Transaction t: pendingTransactions) {
                acc = t.getAccountID();
                amt = t.getAmount();
                CurrentAccount account = accounts.get(acc);
                if (account == null) {//.checks if null
                    System.out.println("Account with "+ acc + "does not exist"); 
                }//IF NULL DO THIS
                else {
                        account.debit(amt);

                    pendingTransactions.remove(t);//REMOVE FROM PLACE
                    completedTransactions.add(t);//ADDT TO COMPLETE
                    accounts.put(acc,account);//CHANGE ACCOUNTS
                }
            }
        }
        System.out.println("Transactions Processed");
    }
    else if (src == btnListClients) { 
      listClients();
    }
    else if (src == btnListAccounts) { 
      listAccounts();
    }
    else if(src == btnPendingTransactions){
        listPendingTransaction();
    }
    else if(src == btnCompleteTransactions){
        listCompleteTransaction();
    }
    else if(src == btnSaveData){
       saveAccount();
       savePendingTransactions();
       btnLoadData.setEnabled(true);
    }
  } // end actionPerformed()
  
  /**
   * Load data from clients.txt using a Scanner; unpack and populate
   *   clients Map.
   */
  public void loadClientData() {
    String fnm="", snm="", pcd="";
    int num=0, id=1;
    try {
      Scanner scnr = new Scanner(new File("clients.txt"));
      scnr.useDelimiter("\\s*#\\s*");
        //fields delimited by '#' with optional leading and trailing spaces
      while (scnr.hasNext()) {
        id  = scnr.nextInt();
        snm = scnr.next();
        fnm = scnr.next();
        num = scnr.nextInt();
        pcd = scnr.next();
        clients.put(new Integer(id), new Client(id, snm, fnm, num, pcd));
      }
      scnr.close();
    } catch (NoSuchElementException e) {
      System.out.printf("%d %s %s %d %s\n", id, snm, fnm, num, pcd);
      JOptionPane.showMessageDialog(this, e.getMessage(),
          "fetch of next token failed ", JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "File Not Found",
          "Unable to open data file", JOptionPane.ERROR_MESSAGE);
    }
    System.out.println("Clients loaded");
  } //end readCustomerData()
  
  // List clients on console
  public void listClients() {
    System.out.println("Clients:");
    for (Client c: clients.values()) {
      System.out.println(c);
    }
    System.out.println();
  } //listCustomers()

  /**
   * Read data from currentAccounts.txt using a Scanner; unpack and populate
   *   accounts Map.
   */
  public void loadAccountData() {
    String id="", srtCd="";
    int onr=0, bal=0, crLm=0;
    try {
      Scanner scnr = new Scanner(new File("currentAccounts.txt"));
      scnr.useDelimiter("\\s*#\\s*");
      while (scnr.hasNext()) {
        id  = scnr.next();
        onr = scnr.nextInt();
        srtCd = scnr.next();
        bal = scnr.nextInt();
        crLm = scnr.nextInt();
        accounts.put(new String(id), new CurrentAccount(id, onr, srtCd, bal, crLm));
      }
      scnr.close();
    } catch (NoSuchElementException e) {
      System.out.printf("%s %d %s, %d, %d\n", id, onr, srtCd, bal, crLm);
      JOptionPane.showMessageDialog(this, e.getMessage(),
          "fetch of next token failed ", JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "File Not Found",
          "Unable to open data file", JOptionPane.ERROR_MESSAGE);
    }
    System.out.println("Account data loaded");
  } //end readItemData()

  // List Accounts on console
  public void listAccounts() {
    System.out.println("Current Accounts:");
    for (CurrentAccount a: accounts.values()) {
      System.out.println(a);
    }
    System.out.println();
  } 
  
  /*
   * this a list of all the pedning tranaction I have made
   * it will iterate through the linked list and print whatever is in it.
   */
  public void listPendingTransaction(){
      Iterator x= pendingTransactions.listIterator();
      System.out.println("pending Tranactions:");
    while (x.hasNext()){
     System.out.println(x.next());
    }
  }
  
  /**
   * thease will be all the completed transaction by going through that linked list
   */
  public void listCompleteTransaction(){
       Iterator z= completedTransactions.listIterator();
       System.out.println("completed Tranactions");
    while(z.hasNext()) {
      System.out.println(z.next());
    }
    System.out.println();
    }
    
    /**
     * this save account method will have a printwriter which 
     * will get everything and save it to the current accounts.txt 
     */
  public void saveAccount(){
  try{  String accountid="";String sortcode="";
    int owner=0,balance=0,creditLimit=0;
   PrintWriter output=new PrintWriter("currentAccounts.txt");
   output.print(accountid);//outputs accounts id
   output.print(" ");
   output.print(owner);//outputs client id
   output.print(" ");
   output.print(sortcode);//outputs sortcode
   output.print(" ");
   output.print(balance);//outputs balance
   output.print(" ");
   output.print(creditLimit);//outputs overdraft
   output.println();
  }catch (FileNotFoundException e){
  }
}

/**
 * this class is very similar to the save account method
 * but instead of saving the accounts
 * it will save the pending transaction
 */ 
  public void savePendingTransactions(){
     try{ String accountid="";
     int amount=0,dateTimestamp=0;
     PrintWriter output=new PrintWriter("transaction.txt");
     output.print(accountid);//outputs firstname
     output.print(" ");
     output.print(amount);//outputs lastName
     output.print(" ");
     output.print(dateTimestamp);//outputs libraryNumber
     output.println();
     output.close();
    }catch(FileNotFoundException e){
    }
}

/**
 * this one will load the pedning ytransaction by using a scnr
 * it will be very similar to the other load method but instead of .put it will add
 * this is because it is linked list not a list
 */
  public void loadPendingTransactions(){
      String accountid="";
        int dateTimestamp=0,amount=0;
    try {
      Scanner scnr = new Scanner(new File("transaction.txt"));
      scnr.useDelimiter("\\s*#\\s*");
        //fields delimited by '#' with optional leading and trailing spaces
      while (scnr.hasNext()) {
        accountid=scnr.next();
        amount=scnr.nextInt();
        dateTimestamp=scnr.nextInt();
        pendingTransactions.add(new Transaction(accountid,amount,dateTimestamp));
      }
      scnr.close();
    } catch (NoSuchElementException e) {
      System.out.printf(" %s %d %d\n",accountid,amount,dateTimestamp);
      JOptionPane.showMessageDialog(this, e.getMessage(),
          "fetch of next token failed ", JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "File Not Found",
          "Unable to open data file", JOptionPane.ERROR_MESSAGE);
    }
    System.out.println("Transactions loaded");
  }

} //end class

