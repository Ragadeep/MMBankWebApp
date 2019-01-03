package com.mmbankapplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;

/**
 * Servlet implementation class MMBankController
 */
@WebServlet("*.mm")
public class MMBankController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestDispatcher dispatcher;
	boolean flag = false;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
			PreparedStatement preparedStatement = connection
					.prepareStatement("DELETE FROM ACCOUNT");
			preparedStatement.execute();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();

		switch (path) {
		case "/newSA.mm":
			response.sendRedirect("openSavingsAccount.html");
			break;

		case "/createAccount.mm":
			String accountHolderName = request
					.getParameter("accountHolderName");
			double accountBalance = Double.parseDouble(request
					.getParameter("accountBalance"));
			boolean salary = request.getParameter("salary").equalsIgnoreCase(
					"no") ? false : true;
			try {
				savingsAccountService.createNewAccount(accountHolderName,
						accountBalance, salary);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;

		case "/closeForm.mm":
			response.sendRedirect("closeAccount.html");
			break;

		case "/closeAccount.mm":
			int deleteAccount = Integer.parseInt(request
					.getParameter("accountNumber"));
			SavingsAccount savingsAccount;
			try {
				savingsAccount = savingsAccountService
						.getAccountById(deleteAccount);
				savingsAccountService.delete(savingsAccount);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/depositForm.mm":
			response.sendRedirect("depositForm.html");
			break;

		case "/depositAmmount.mm":
			int accountNumber = Integer.parseInt(request
					.getParameter("accountNumber"));
			double depositAmmount = Double.parseDouble(request
					.getParameter("depositAmmount"));
			try {
				savingsAccount = savingsAccountService
						.getAccountById(accountNumber);
				savingsAccountService.deposit(savingsAccount, depositAmmount);
				DBUtil.commit();
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			break;

		case "/update.mm":
			response.sendRedirect("update.html");
			break;

		case "/updateForm.mm":
			int accountid = Integer.parseInt(request.getParameter("accountNumber"));

			SavingsAccount accountUpdate;
			try {
				accountUpdate = savingsAccountService.getAccountById(accountid);
				request.setAttribute("accounts", accountUpdate);
				dispatcher = request.getRequestDispatcher("updateDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
			} catch (SQLException e2) {
				e2.printStackTrace();
			} catch (AccountNotFoundException e2) {
				e2.printStackTrace();
			}
			break;
			
		case "/updateAccount.mm":
			int accountId = Integer.parseInt(request.getParameter("accountnumber"));
			try {
				accountUpdate = savingsAccountService.getAccountById(accountId);
				String accHName = request.getParameter("accountHolderName");
				accountUpdate.getBankAccount().setAccountHolderName(accHName);
				double accBal = Double.parseDouble(request.getParameter("accountBalance"));
				boolean isSalary = request.getParameter("salary").equalsIgnoreCase("no")?false:true;
				accountUpdate.setSalary(isSalary);
				savingsAccountService.updateAccount(accountUpdate);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e2) {
				e2.printStackTrace();
			}
			break;

		case "/withdrawForm.mm":
			response.sendRedirect("withdrawForm.html");
			break;

		case "/withdrawAmmount.mm":
			int accNumber = Integer.parseInt(request
					.getParameter("accountNumber"));
			double withdrawAmmount = Double.parseDouble(request
					.getParameter("withdrawAmmount"));
			try {
				savingsAccount = savingsAccountService
						.getAccountById(accNumber);
				savingsAccountService.withdraw(savingsAccount, withdrawAmmount);
				DBUtil.commit();
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (Exception e) {
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			break;

		case "/fundstransferFrom.mm":
			response.sendRedirect("fundstransfer.html");
			break;

		case "/fundsTransfer.mm":
			int senderaccNumber = Integer.parseInt(request
					.getParameter("sendersAccount"));
			int receiveraccNumber = Integer.parseInt(request
					.getParameter("receicersAccount"));
			double transferAmmount = Double.parseDouble(request
					.getParameter("ammount"));
			try {
				SavingsAccount senderSavingsAccount = savingsAccountService
						.getAccountById(senderaccNumber);
				SavingsAccount receiverSavingsAccount = savingsAccountService
						.getAccountById(receiveraccNumber);
				savingsAccountService.fundTransfer(senderSavingsAccount,
						receiverSavingsAccount, transferAmmount);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "/currentBalance.mm":
			response.sendRedirect("currentBalance.html");
			break;

		case "/checkBalance.mm":
			int checkBal = Integer.parseInt(request
					.getParameter("accountNumber"));
			try {
				double checkAccBal = savingsAccountService
						.checkBalance(checkBal);
				PrintWriter out = response.getWriter();
				out.println("Yout Account Balance: " + checkAccBal);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "/searchForm.mm":
			response.sendRedirect("searchForm.html");
			break;

		case "/search.mm":
			int accountSearch = Integer.parseInt(request
					.getParameter("txtAccountNumber"));
			try {
				SavingsAccount account = savingsAccountService
						.getAccountById(accountSearch);
				request.setAttribute("account", account);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "/getAll.mm":
			try {
				List<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				request.setAttribute("accounts", accounts);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/sortByName.mm":
			flag = !flag;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {

					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = arg0
								.getBankAccount()
								.getAccountHolderName()
								.compareTo(
										arg1.getBankAccount()
												.getAccountHolderName());

						if (flag == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "/sortByNumber.mm":
			flag = !flag;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {

					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = arg0.getBankAccount().getAccountNumber()
								- (arg1.getBankAccount().getAccountNumber());

						if (flag == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			break;

		case "/sortByBalance.mm":
			flag = !flag;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {

					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = (int) (arg0.getBankAccount()
								.getAccountBalance() - arg1.getBankAccount()
								.getAccountBalance());

						if (flag == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			break;

		case "/sortBySalary.mm":
			flag = !flag;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {

					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = Boolean.compare(arg0.isSalary(),
								arg1.isSalary());

						if (flag == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
	

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
