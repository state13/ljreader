package ru.state13.cn1.ljreader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.InfiniteScrollAdapter;
import com.codename1.components.MultiButton;
import com.codename1.components.WebBrowser;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Cookie;
import com.codename1.io.NetworkManager;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Label;
import com.codename1.ui.SideMenuBar;
import com.codename1.ui.Slider;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.BrowserNavigationCallback;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.NumericSpinner;
import com.codename1.ui.util.ImageIO;
import com.codename1.ui.util.Resources;
import com.codename1.util.StringUtil;

import me.regexp.RE;

public class LJreader {
	private String lj_login;
	private String lj_password;
	private Vector<String> user_login;
	private String lj_flat_url;
	private String lj_journal_name, m_lj_journal_name;
	private HashMap<String, String> lj_user, m_user;
	private String selectedURL;
	private String selectedID;
	private String selectedTag;
	private int selectedTagItems;
	private String selectedItem;
	private String savedItemId;
	private HashMap<String, ArrayList<SimpleEntry<String, Integer>>> tagsListAll;
	private HashMap<String, ArrayList<MyItem>> itemsListAll;
	private HashMap<String, HashMap<String, HashMap<String, String>>> savedItems;
	private String wbPage, wbPageOriginal;
	private LinkedHashMap<String, HashMap<String, String>> lj_users_list, lj_communities_list;
	private boolean isTags, isUsers, isBusy, tagsSortA, tagsSortedA, longPressed, isPinchToZoom, isShowHide, isIOS,
			saveEnabled, isShowRemove, isSaving, isErrorShowed, isRegistered, isAuthorizing, isNetworkError, isTop,
			isFriends, isSaved, isFilterAdded, isFallToSimpleTags;
	private Boolean isNight;
	private int totalEntr, maxTags;
	private String tagsNotFound, templateErrorTitle, templateError;
	// private String darkHtmlHeader;
	// private String lightHtmlHeader;
	private String htmlFooter;
	private int itemsSkipNum;
	private String lightTheme, darkTheme;
	private Component clickedItem, clickedTagItem;
	private Vector<String> listUsers, listCommunities, listHiddenUsers, listHiddenCommunities;
	private Form current;
	private HashMap<String, String> mimeMap;
	private MyItem currentMyItem;
	private MForm communitiesForm, tagsForm, itemsForm, savedItemsForm, browserForm;
	Resources theme;
	private String authChallenge, authResponse;
	private HashMap<String, HashMap<String, String>> owner_users_list, owner_communities_list;
	private HashMap<String, String> nameConv;
	private LinkedHashMap<String, String> friendsFilter;
	private String friendsSelectedFilter;
	private String firstPage;
	private Double fontSize;

	// private int progress;

	// private void showStorage() {
	// myLog("---------Storage-----------");
	// String[] s = Storage.getInstance().listEntries();
	// for (int i = 0; i < s.length; i++) {
	// myLog(s[i]);
	// }
	// myLog("---------Storage End-----------");
	// }

	@SuppressWarnings("unchecked")
	public void init(Object context) {

		isIOS = "ios".equals(Display.getInstance().getPlatformName());
		// myLog(Display.getInstance().getPlatformName());
		if (isIOS)
			lightTheme = "iOS7";
		else
			lightTheme = "ics_light";

		darkTheme = "Android_Theme";

		isNight = (Boolean) Storage.getInstance().readObject("isNight");
		if (isNight == null || isIOS)
			isNight = false;

		fontSize = (Double) Storage.getInstance().readObject("fontSize");
		if (fontSize == null)
			fontSize = 0d;

		// friendsSelectedFilter = (String)
		// Storage.getInstance().readObject("friends_filter_selected");

		// friendsFilter = (HashMap<String,String>)
		// Storage.getInstance().readObject("friends_filter");
		// if (friendsSelectedFilter == null)
		// friendsSelectedFilter = false;

		user_login = (Vector<String>) Storage.getInstance().readObject("user_login");

		// Util.register("MyItem", MyItem.class);
		// showStorage();
		// Storage.getInstance().clearStorage();
		savedItems = (HashMap<String, HashMap<String, HashMap<String, String>>>) Storage.getInstance()
				.readObject("savedItems");
		if (savedItems == null)
			savedItems = new HashMap<String, HashMap<String, HashMap<String, String>>>();

		try {
			theme = Resources.openLayered("/theme");

			UIManager.getInstance().setThemeProps(theme.getTheme(isNight ? darkTheme : lightTheme));
			UIManager.getInstance().getLookAndFeel().setMenuBarClass(SideMenuBar.class);
			Display.getInstance().setCommandBehavior(Display.COMMAND_BEHAVIOR_SIDE_NAVIGATION);

		} catch (IOException e) {
			e.printStackTrace();
		}

		mimeMap = new HashMap<String, String>() {
			{
				put("JPG", "image/jpeg");
				put("JPEG", "image/jpeg");
				put("GIF", "image/gif");
				put("PNG", "image/png");
			}
		};
		savedItemId = null;

		nameConv = new HashMap<String, String>();
		nameConv.put("top", "Топ");
		nameConv.put("friends", "Лента");
		isFilterAdded = false;
		isTop = false;
		isFriends = false;
		authChallenge = null;
		authResponse = null;
		isRegistered = false;
		// isMemoryErrorShowed = false;
		isNetworkError = false;
		isAuthorizing = false;
		isPinchToZoom = false;
		isSaving = false;
		isErrorShowed = false;
		isShowHide = false;
		isShowRemove = false;
		isFallToSimpleTags = false;
		saveEnabled = false;
		lj_login = "";//default account for anonymouse reading login
		lj_password = "";//default password
		friendsSelectedFilter = null;
		firstPage = null;

		lj_flat_url = "http://www.livejournal.com/interface/flat";
		lj_journal_name = "";

		htmlFooter = "</body></html>";

		selectedURL = "";
		selectedID = "";
		selectedTag = "";

		selectedItem = "";
		tagsListAll = new HashMap<String, ArrayList<SimpleEntry<String, Integer>>>();
		itemsListAll = new HashMap<String, ArrayList<MyItem>>();
		wbPage = "";

		totalEntr = 10000;
		maxTags = 300;
		templateError = "Записи не найдены.";
		templateErrorTitle = "Ошибка данных";
		tagsNotFound = "Теги не найдены.";
		itemsSkipNum = 10;
		clickedItem = null;
		clickedTagItem = null;

		isBusy = false;
		tagsSortA = false;
		tagsSortedA = false;
		longPressed = false;

		lj_users_list = Journals.getLjUsersList();
		lj_communities_list = Journals.getLjCommunitiesList();

		owner_users_list = (HashMap<String, HashMap<String, String>>) Storage.getInstance()
				.readObject("owner_users_list");
		if (owner_users_list == null)
			owner_users_list = new LinkedHashMap<String, HashMap<String, String>>();

		owner_communities_list = (HashMap<String, HashMap<String, String>>) Storage.getInstance()
				.readObject("owner_communities_list");
		if (owner_communities_list == null)
			owner_communities_list = new LinkedHashMap<String, HashMap<String, String>>();

		lj_communities_list.putAll(owner_communities_list);
		lj_users_list.putAll(owner_users_list);

		listUsers = loadList("Users");
		Vector<String> tmp;
		if (listUsers != null) {
			tmp = new Vector<String>(listUsers);
			for (String k : tmp) {
				if (!lj_users_list.containsKey(k))
					listUsers.remove(k);
			}
		}

		listCommunities = loadList("Communities");
		if (listCommunities != null) {
			tmp = new Vector<String>(listCommunities);
			for (String k : tmp) {
				if (!lj_communities_list.containsKey(k))
					listCommunities.remove(k);
			}
		}

		listHiddenUsers = loadList("HiddenUsers");
		listHiddenCommunities = loadList("HiddenCommunities");

		// lj_users_list_tag = Journals.getLjUsersListTag();
		// lj_communities_list_tag = Journals.getLjCommunitiesListTag();

		// communitiesForm = newMyForm("Сообщества", null);
		// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());

		// usersForm = newMyForm("Блогеры", null);
		// tagsForm = newMyForm("Записи", null);

		Display.getInstance().setProperty("WebLoadingHidden", "true");
		resetTransitions();
		Cookie.clearCookiesFromStorage();
	}

	private String getDarkHeader() {
		return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body bgcolor=\"#000000\" text=\"#FFFFFF\" link=\"#FFCC00\" style=\"font-size: "
				+ (16 + fontSize) + "px\">";
	}

	private String getLightHeader() {
		return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body bgcolor=\"#FFFFFF\" text=\"#000000\" style=\"font-size: "
				+ (16 + fontSize) + "px\">";
	}

	public void resetTransitions() {
		UIManager.getInstance().getLookAndFeel().setDefaultFormTransitionOut(CommonTransitions.createEmpty());
		UIManager.getInstance().getLookAndFeel().setDefaultFormTransitionIn(CommonTransitions.createEmpty());
	}

	public void myLog(String str) {
		// System.out.println(str);
	}

	public void start() {
		if (current != null) {
			current.show();
			return;
		} else {
			// MyForm f = newMyForm("Main", null);
			// f.setLayout(new BorderLayout());
			// f.setCustomTitleArea("Main");
			if (communitiesForm == null) {
				communitiesForm = newMyForm("Сообщества", null);
				communitiesForm.addShowListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						resetUser();
					}
				});
			}

			beforeMain(communitiesForm);
			communitiesForm.show();

			// myLog(((SideMenuBar)communitiesForm.getMenuBar()).getCommandCount());
			if (((SideMenuBar) communitiesForm.getMenuBar()).getCommandCount() <= 0)
				addMainCommands(communitiesForm);

			postMain(communitiesForm);
		}
		// new StateMachine("/theme");
	}

	public void stop() {
		current = Display.getInstance().getCurrent();
	}

	public void destroy() {
	}

	private void addTags(final Container c, final ArrayList<SimpleEntry<String, Integer>> tagsList) {

		c.removeAll();
		// c.revalidate();
		// Resources r = fetchResourceFile();
		if (tagsList.size() > 0) {
			// int i = 0;

			for (SimpleEntry<String, Integer> entry : tagsList) {
				MultiButton mb = new MultiButton();
				mb.setEmblem(null);
				mb.setHorizontalLayout(true);
				mb.setTextLine1(entry.getKey());
				if (entry.getValue() != 0)
					mb.setTextLine2(entry.getValue().toString());
				// mb.setTextLine3(entry.getKey() + "3");
				// mb.setTextLine4(entry.getKey() + "4");

				// mb.setMaskName("maskImage");
				// mb.setIconUIID("Avatar");
				// mb.setIcon(r.getImage(C_AVATAR[iter]));

				final SimpleEntry<String, Integer> e = entry;
				mb.setCommand(new Command("") {
					public void actionPerformed(ActionEvent ev) {
						// if (!isBusy) {
						// isBusy = true;
						// itemsListTag.clear();
						// selectedURL = "http://" + lj_journal_name +
						// ".livejournal.com/";

						selectedURL = "http://m.livejournal.com/read/user/" + m_lj_journal_name + "/";
						selectedTag = e.getKey();
						clickedItem = null;
						// itemsSkipNum = 10;
						totalEntr = 10000;
						if (e.getValue() != 0)
							selectedTagItems = e.getValue();
						else
							selectedTagItems = 10000;
						showItemsForm((MForm) c.getComponentForm());
						// showForm("Items", null);

						// }
					}
				});

				c.addComponent(mb);

				if (entry.getKey() == selectedTag) {
					// myLog(mb);
					// myLog("selectedTag "+selectedTag);
					clickedTagItem = mb;
					// myLog(clickedTagItem);
				}

			}

			// c.revalidate();
		} else {
			// System.out.println(selectedURL);
			Dialog.show(templateErrorTitle, templateError, "OK", null);
			// back();
		}
		isBusy = false;
	}

	private void sortTags(final Container c, final ArrayList<SimpleEntry<String, Integer>> tagsList) {
		if (tagsList.size() > 0) {
			if (!tagsSortedA && tagsSortA) {

				// Thread t = new Thread() {
				// public void run() {
				InfiniteProgress ip = new InfiniteProgress();
				final Dialog dlg = ip.showInifiniteBlocking();
				Collections.sort(tagsList, new Comparator<SimpleEntry<String, Integer>>() {
					public int compare(SimpleEntry<String, Integer> o1, SimpleEntry<String, Integer> o2) {
						return o1.getKey().compareTo(o2.getKey());
					}
				});
				addTags(c, tagsList);
				setTagCommands(c);
				tagsSortedA = true;
				if (dlg != null)
					dlg.dispose();
				// }
				// };
				// t.run();

			} else if (tagsSortedA && !tagsSortA) {

				// Thread t = new Thread() {
				// public void run() {
				InfiniteProgress ip = new InfiniteProgress();
				final Dialog dlg = ip.showInifiniteBlocking();
				Collections.sort(tagsList, new Comparator<SimpleEntry<String, Integer>>() {
					public int compare(SimpleEntry<String, Integer> o1, SimpleEntry<String, Integer> o2) {
						return -(o1.getValue() - o2.getValue());
					}
				});
				addTags(c, tagsList);
				setTagCommands(c);
				tagsSortedA = false;
				if (dlg != null)
					dlg.dispose();
				// }
				// };
				// t.run();

			}
		}
	}

	private void setTagCommands(final Container c) {
		// c.getComponentForm().removeAllCommands();
		((MForm) c.getComponentForm()).removeAllCustomCommands();

		if (!tagsSortA) {
			Command comSort = new Command("А-я", theme.getImage("4_collections_sort_by_size.png")) {
				public void actionPerformed(ActionEvent ev) {
					// if (!isBusy) {
					// isBusy = true;
					clickedItem = null;
					tagsSortA = true;
					// clickedTagItem=null;
					// c.removeAll();
					// showTags(c, tagsListAll.get(selectedURL) == null ? new
					// ArrayList<SimpleEntry<String, Integer>>() :
					// tagsListAll.get(selectedURL));
					sortTags(c, tagsListAll.get(selectedURL) == null ? new ArrayList<SimpleEntry<String, Integer>>()
							: tagsListAll.get(selectedURL));
					// }
				}
			};

			// comSort.putClientProperty("TitleCommand", Boolean.TRUE);
			((MForm) c.getComponentForm()).addCustomCommand(comSort);
		} else {
			Command comSort = new Command("0-9", theme.getImage("4_collections_sort_by_size.png")) {
				public void actionPerformed(ActionEvent ev) {
					// if (!isBusy) {
					// isBusy = true;
					clickedItem = null;
					tagsSortA = false;
					// clickedTagItem=null;
					// c.removeAll();
					// showTags(c, tagsListAll.get(selectedURL) == null ? new
					// ArrayList<SimpleEntry<String, Integer>>() :
					// tagsListAll.get(selectedURL));
					sortTags(c, tagsListAll.get(selectedURL) == null ? new ArrayList<SimpleEntry<String, Integer>>()
							: tagsListAll.get(selectedURL));
					// }
				}
			};
			// comSort.putClientProperty("TitleCommand", Boolean.TRUE);
			((MForm) c.getComponentForm()).addCustomCommand(comSort);
		}

		Command com = new Command("Записи", theme.getImage("4_collections_view_as_list.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				// isBusy = true;
				selectedTagItems = 10000;
				isTags = false;
				// selectedTag = "";
				clickedTagItem = null;
				clickedItem = null;
				// if (tagsForm == null)
				// tagsForm = newMyForm("Записи", (MForm)
				// Display.getInstance().getCurrent());

				// if ((MForm) c.getComponentForm()==null){
				// showTagsForm(communitiesForm);
				// } else {
				showEntries(c);
				// }
				// }
			}
		};

		// com.putClientProperty("TitleCommand", Boolean.TRUE);
		((MForm) c.getComponentForm()).addCustomCommand(com);
		((MForm) c.getComponentForm()).setBackCommand(com);
		c.getComponentForm().revalidate();
	}

	private void showTags(final Container c, final ArrayList<SimpleEntry<String, Integer>> tagsList) {
		myLog("showTags");
		// itemsList.clear();
		// itemsSkipNum = 10;
		totalEntr = 10000;

		// myLog(c.getComponentCount());

		if (tagsList.size() <= 0) {
			myLog("Getting tags...");
			// //if (authChallenge == null || authResponse == null) {
			// setChallengeResponse();
			// //}

			InfiniteProgress ip = new InfiniteProgress();
			final Dialog dlg = ip.showInifiniteBlocking();
			// dlg.setTimeout(10 * 1000);

			MyConnectionRequest requestElement = new MyConnectionRequest() {

				@Override
				protected String initCookieHeader(String cookie) {
					// myLog("cookie=" + cookie);
					//
					// if (!isRegistered) {
					// String cookie2 = null;
					// if (cookie != null && !(cookie.indexOf("langpref=") > 0))
					// cookie2 =
					// "rating_show_custom=1; langpref=ru/1385193547; " +
					// cookie;
					// if (cookie == null)
					// cookie2 = "rating_show_custom=1; langpref=ru/1385193547";
					//
					// return cookie2;
					// } else {
					// return cookie;
					// }

					return "rating_show_custom=1; langpref=ru/1469450983;";

				}

				@Override
				protected void postResponse() {
					myLog("Register postResponse");
					if (tagsList.size() > 0) {
						tagsListAll.put(getMyURL(), tagsList);
						setTagCommands(c);
						if (getMyURL() == selectedURL && isTags)
							addTags(c, tagsList);

						if (dlg != null)
							dlg.dispose();

						if (c.getComponentCount() > 0)
							c.scrollRectToVisible(0, 0, 1, c.getHeight(), c.getComponentAt(0));
						((MForm) c.getComponentForm()).setCustomTitle(lj_journal_name + " - теги");
						isFallToSimpleTags = false;
					} else {
						if (isFallToSimpleTags) {
							isFallToSimpleTags = false;
							if (dlg != null)
								dlg.dispose();
							Dialog.show("", tagsNotFound, "OK", null);
						} else {
							isFallToSimpleTags = true;
							if (dlg != null)
								dlg.dispose();
							showTags(c, new ArrayList<SimpleEntry<String, Integer>>());
						}
					}
				}

				protected void readResponse(InputStream input) throws IOException {

					String response = Util.readToString(input, "UTF-8");
					Util.cleanup(input);

					String jname = StringUtil.replaceAll(lj_journal_name, "_", "-");

					if (isFallToSimpleTags) {
						lj_user = lj_communities_list.get("friends");
					} else {
						if (lj_users_list.keySet().contains(jname))
							lj_user = lj_users_list.get(jname);
						else if (lj_communities_list.keySet().contains(jname))
							lj_user = lj_communities_list.get(jname);
						else
							lj_user = lj_communities_list.get("friends");
					}

					// if (lj_user.get("tag_start") == null)
					// lj_user = lj_communities_list.get("friends");
					// if (isUsers) {
					// if (owner_users_list.keySet().contains(jname) ||
					// !lj_users_list.keySet().contains(jname)) {
					// lj_user = lj_communities_list.get("friends");
					// } else {
					// lj_user = lj_users_list.get(jname);
					// }
					// } else {
					// if (owner_communities_list.keySet().contains(jname) ||
					// !lj_communities_list.keySet().contains(jname)) {
					// lj_user = lj_communities_list.get("friends");
					// } else {
					// lj_user = lj_communities_list.get(jname);
					// }
					// }
					int i = 0;
					RE pattern;
					if (isFallToSimpleTags || (lj_user.get("tag_num") != null && lj_user.get("tag_num").length() > 0)) {
						pattern = new RE(lj_user.get("tag_start"));
					} else {
						pattern = new RE("<ul class=\"j-w-list j-w-list-tags j-p-tagcloud\">");
					}

					if (pattern.match(response))
						i = pattern.getParenEnd(0);
					// System.out.println(i+response);
					RE pattern2;
					if (isFallToSimpleTags || (lj_user.get("tag_num") != null && lj_user.get("tag_num").length() > 0)) {
						pattern = new RE(lj_user.get("tag_name"));
						pattern2 = new RE(lj_user.get("tag_num"));
					} else {
						pattern = new RE(
								"<li.*?><a href=\"http://" + jname + "\\.livejournal\\.com/tag/.*?>(.*?)</a>.*?</li>");
						pattern2 = new RE(
								"<a href=\"http://" + jname + "\\.livejournal\\.com/tag/.*?title=\"([0-9]+).*?\">");
					}

					// RE pattern2 = new RE(lj_user.get("tag_num"));
					// RE pattern2 = new RE("<a
					// href=\"http://"+lj_journal_name+"\\.livejournal\\.com/tag/.*?title=\"([0-9]+).*?\">");
					RE patternH = new RE("(<.*?>)|(&[^;]+?;)");
					// System.out.println("tag_num: " + lj_user.get("tag_num"));
					// .*?<h3
					// class=\"entry-header\">(.*?)</h3>.*?<a
					// class=\"permalink\" href=\"(.*?)\">
					SimpleEntry<String, Integer> te;
					for (String s : lj_user.keySet()) {
						System.out.println(s + ":" + lj_user.get(s));
					}
					try {
						// int cc=0;
						while (pattern.match(response, i)) {
							// System.out.println(lj_user.get("tag_num"));
							// if (lj_user.get("tag_num").length() > 0) {
							if (lj_user.get("tag_num") == null
									|| (lj_user.get("tag_num") != null && lj_user.get("tag_num").length() > 0)) {
								if (pattern2.match(response, i)) {
									// System.out.println(pattern2.getParen(1));
									if (Integer.parseInt(pattern2.getParen(1)) > 0) {
										te = new SimpleEntry<String, Integer>(
												patternH.subst(pattern.getParen(1), "").trim(),
												Integer.parseInt(pattern2.getParen(1)));
										if (!tagsList.contains(te))
											tagsList.add(te);
									}
								}
							} else {
								te = new SimpleEntry<String, Integer>(
										Util.decode(patternH.subst(pattern.getParen(1), "").trim(), "UTF-8", false), 0);
								if (!tagsList.contains(te))
									tagsList.add(te);
							}
							// if (cc<30){
							// System.out.println(response.substring(pattern.getParenStart(0)-200,
							// pattern.getParenEnd(0)));
							// cc++;
							// }
							i = pattern.getParenEnd(0);

						}

					} catch (Exception ex) {
						ex.printStackTrace();
						if (dlg != null)
							dlg.dispose();
						// break;
					} catch (Error er) {
						er.printStackTrace();
						if (dlg != null)
							dlg.dispose();
						// break;
					}
					Collections.sort(tagsList, new Comparator<SimpleEntry<String, Integer>>() {
						public int compare(SimpleEntry<String, Integer> o1, SimpleEntry<String, Integer> o2) {
							return -(o1.getValue() - o2.getValue());

						}
					});

					tagsSortedA = false;

					if (tagsList.size() > maxTags)
						tagsList.subList(maxTags, tagsList.size()).clear();

				}
			};

			requestElement
					.setUrl("http://" + StringUtil.replaceAll(lj_journal_name, "_", "-") + ".livejournal.com/tag/");
			myLog("http://" + StringUtil.replaceAll(lj_journal_name, "_", "-") + ".livejournal.com/tag/");
			// requestElement.removeAllArguments();
			requestElement.setPost(false);
			requestElement.setMyURL(selectedURL);
			// requestElement.setFollowRedirects(false);

			isNetworkError = false;
			NetworkManager.getInstance().addToQueue(requestElement);
			// runSingleRequest(requestElement);

			// InfiniteProgress ip = new InfiniteProgress();
			// final Dialog dlg = ip.showInifiniteBlocking();
			//
			// // findMyWebBrowser(f).setURL(selectedURL);
			// MyConnectionRequest requestElement = new MyConnectionRequest() {
			//
			// // InfiniteProgress ip = new InfiniteProgress();
			// // Dialog dlg = ip.showInifiniteBlocking();
			//
			// String resp = "";
			//
			// @Override
			// protected void postResponse() {
			//
			// if (dlg != null)
			// dlg.dispose();
			// // f.revalidate();
			// }
			//
			// protected void readResponse(InputStream input) throws IOException
			// {
			//
			// // resp="";
			// String response = Util.readToString(input, "UTF-8");
			// Util.cleanup(input);
			// String[] responseArr = Util.split(response, "\n");
			// // tagsList.add(new SimpleEntry<String,
			// // Integer>(patternH.subst(pattern.getParen(1), "").trim(),
			// // Integer.parseInt(pattern2.getParen(1))));
			// myLog(response);
			//
			// }
			// };
			//
			// requestElement.setUrl(lj_flat_url);
			//
			// requestElement.removeAllArguments();
			// requestElement.setPost(true);
			//
			// requestElement.addArgument("mode", "getusertags");
			// requestElement.addArgument("auth_method", "challenge");
			// requestElement.addArgument("auth_challenge", authChallenge);
			// requestElement.addArgument("auth_response", authResponse);
			//
			// if (user_login != null && user_login.size() > 1)
			// requestElement.addArgument("user", user_login.get(0));
			// else
			// requestElement.addArgument("user", lj_login);
			//
			// if ("top".equals(lj_journal_name) ||
			// "friends".equals(lj_journal_name))
			// requestElement.addArgument("usejournal",
			// currentMyItem.getAuthor());
			// else
			// requestElement.addArgument("usejournal", lj_journal_name);
			//
			// requestElement.addArgument("ver", "1");
			//
			// isNetworkError = false;
			// NetworkManager.getInstance().addToQueue(requestElement);

		} else {

			setTagCommands(c);
			addTags(c, tagsList);

			if (c.getComponentCount() > 0)
				c.scrollRectToVisible(0, 0, 1, c.getHeight(), c.getComponentAt(0));

			if (clickedTagItem != null) {
				// myLog("clickedTagItem "+clickedTagItem);
				c.scrollComponentToVisible(clickedTagItem);
			} else {
				// myLog(c.getComponentAt(0));
				c.scrollComponentToVisible(c.getComponentAt(0));
			}
			((MForm) c.getComponentForm()).setCustomTitle(lj_journal_name + " - теги");
		}

	}

	private void showEntries(final Container c) {
		myLog("showEntries");
		// itemsList.clear();
		c.removeAll();
		// itemsSkipNum = 10;
		totalEntr = 10000;

		((MForm) c.getComponentForm()).setCustomTitle("Записи");

		// c.getComponentForm().removeAllCommands();
		((MForm) c.getComponentForm()).removeAllCustomCommands();

		Command tags = new Command("Теги", theme.getImage("4_collections_labels.png")) {
			public void actionPerformed(ActionEvent ev) {
				if (!isBusy) {
					// tagsSortA=false;
					// isBusy = true;
					isTags = true;
					// showTagsForm(itemsForm);
					// if((Container) tagsForm.getClientProperty("mainc")!=null)
					showTags(c, tagsListAll.get(selectedURL) == null ? new ArrayList<SimpleEntry<String, Integer>>()
							: tagsListAll.get(selectedURL));
				}
			}
		};

		// tags.putClientProperty("TitleCommand", Boolean.TRUE);
		((MForm) c.getComponentForm()).addCustomCommand(tags);

		Command reload = new Command("Обновить", theme.getImage("1_navigation_refresh.png")) {
			public void actionPerformed(ActionEvent ev) {
				if (!isBusy) {
					c.removeAll();
					showItems(c, new ArrayList<MyItem>());
					if (c.getComponentCount() > 0)
						c.scrollRectToVisible(0, 0, 1, c.getHeight(), c.getComponentAt(0));
				}
			}
		};

		// reload.putClientProperty("TitleCommand", Boolean.TRUE);
		((MForm) c.getComponentForm()).addCustomCommand(reload);

		Command back = new Command("Назад") {
			public void actionPerformed(ActionEvent ev) {
				if (selectedTag.length() > 0 && tagsForm != null && !((MForm) c.getComponentForm()).equals(tagsForm)) {
					tagsForm.showBack();
				} else {

					communitiesForm.showBack();
				}
				// communitiesForm.revalidate();
			}
		};
		// myLog("showItemsForm " + selectedTag);
		((MForm) c.getComponentForm()).setBackCommand(back);

		c.getComponentForm().revalidate();

		showItems(c, itemsListAll.get(selectedURL + selectedTag) == null ? new ArrayList<MyItem>()
				: itemsListAll.get(selectedURL + selectedTag));
		// f.revalidate();

	}

	private MForm newMyForm(String title, final MForm parent) {
		MForm f = new MForm();
		f.setCustomTitle(title);
		// f.setLayout(new BorderLayout());
		if (parent != null) {
			Command back = new Command(parent.getTitle()) {
				public void actionPerformed(ActionEvent ev) {
					parent.showBack();
					parent.revalidate();
				}
			};
			// myLog("showItemsForm " + selectedTag);
			f.setBackCommand(back);
		} else {
			Command exit = new Command("Выйти") {
				public void actionPerformed(ActionEvent ev) {

					if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
						Display.getInstance().exitApplication();
					}

				}
			};
			// placeCommand(exit,"right");

			f.setBackCommand(exit);
		}

		return f;
	}

	private Container addLayoutY(MForm f) {
		f.removeAll();
		Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		c.setScrollableY(true);
		f.addComponent(BorderLayout.CENTER, c);
		return c;
	}

	protected void showTagsForm(final MForm parent) {
		myLog("showTagsForm");
		selectedTagItems = 10000;
		isFallToSimpleTags = false;

		if (tagsForm == null) {
			tagsForm = newMyForm("Записи", parent);
		}
		if (parent != null) {
			Command back = new Command(parent.getTitle()) {
				public void actionPerformed(ActionEvent ev) {
					if (parent.equals(browserForm)) {
						if (browserForm.getClientProperty("myWb") != null) {
							parent.showBack();
							parent.revalidate();
						} else {
							// resetUser();
							communitiesForm.showBack();
						}

					} else if (parent.equals(tagsForm)) {
						// resetUser();
						communitiesForm.showBack();
					} else {
						parent.showBack();
					}
				}
			};
			// myLog("showItemsForm " + selectedTag);
			tagsForm.setBackCommand(back);
		} else {
			Command back = new Command("") {
				public void actionPerformed(ActionEvent ev) {

					// resetUser();
					communitiesForm.showBack();
				}
			};
			// myLog("showItemsForm " + selectedTag);
			tagsForm.setBackCommand(back);
		}
		Container c = addLayoutY(tagsForm);
		tagsForm.putClientProperty("mainc", c);
		tagsForm.revalidate();
		tagsForm.show();

		// ((CustomSideMenuBar) f.getMenuBar()).removeAllSideCommands();
		// addMainCommands(tagsForm);
		if (((SideMenuBar) tagsForm.getMenuBar()).getCommandCount() <= 0)
			addMainCommands(tagsForm);

		if (isTags)
			showTags(c, tagsListAll.get(selectedURL) == null ? new ArrayList<SimpleEntry<String, Integer>>()
					: tagsListAll.get(selectedURL));
		else
			showEntries(c);

	}

	private void setFriendsCommands(final Container c) {
		final MForm f = (MForm) c.getComponentForm();
		f.removeAllCustomCommands();

		Command friends = new Command("Обновить", theme.getImage("1_navigation_refresh.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				isUsers = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				if (isRegistered) {
					c.removeAll();
					showItems(c, new ArrayList<MyItem>());
				} else {
					showFriends(communitiesForm);
				}
				// showItems(c, itemsListAll.get(selectedURL) == null ? new
				// ArrayList<MyItem>() : itemsListAll.get(selectedURL));
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Авторизация", theme.getImage("1_lock.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				login();
			}
		};
		f.addCustomCommand(topUsers);

		Command users = new Command("Блогеры", theme.getImage("6_social_group.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				isUsers = true;
				showUsers(f);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(users);
	}

	private void showFriends(final MForm f) {
		// itemsSkipNum = 10;
		totalEntr = 10000;
		isUsers = false;
		isTags = false;
		isTop = false;
		isFilterAdded = false;
		savedItemId = null;
		selectedTag = "";
		selectedItem = "";
		selectedTagItems = 10000;
		lj_journal_name = "friends";
		m_lj_journal_name = "friends";
		selectedURL = "http://m.livejournal.com/read/friends/";
		if (communitiesForm == null)
			communitiesForm = newMyForm("Лента", null);

		communitiesForm.removeAll();
		final Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		c.setScrollableY(true);
		Container wc = new Container(new BorderLayout());
		wc.addComponent(BorderLayout.CENTER, c);
		communitiesForm.addComponent(BorderLayout.CENTER, wc);
		communitiesForm.putClientProperty("FilterContainer", wc);

		// final Container c = addLayoutY(communitiesForm);
		setFriendsCommands(c);

		communitiesForm.setCustomTitle("Лента");
		// if (user_login == null || user_login.equals(false_user_login)) {
		// login();
		// }

		myLog("isRegistered=" + isRegistered);

		if (isRegistered == true) {
			c.removeAll();
			showItems(c,
					itemsListAll.get(selectedURL) == null ? new ArrayList<MyItem>() : itemsListAll.get(selectedURL));

		} else {
			if (user_login != null && user_login.size() > 1 && user_login.get(0).trim().length() > 0
					&& user_login.get(1).trim().length() > 0) {
				Slider slider = new Slider();
				slider.setInfinite(true);
				slider.setEditable(false);
				c.addComponent(slider);
				// c.getComponentForm().putClientProperty("iSlider", slider);
				authorizeUser();
				// if (c.getComponentForm().getClientProperty("iSlider") !=
				// null)
				// c.getComponentForm().removeComponent((Component)
				// c.getComponentForm().getClientProperty("iSlider"));
				if (!isRegistered) {
					login();
				} else {
					c.removeAll();
					showItems(c, itemsListAll.get(selectedURL) == null ? new ArrayList<MyItem>()
							: itemsListAll.get(selectedURL));
				}
			} else {
				login();
			}
			// if (user_login != null)
			// Dialog.show("Ошибка", "Неправильный логин или пароль", "OK",
			// null);
			// login();
		}
		communitiesForm.setCustomTitle("Лента");

	}

	private void login() {

		// myLog(user_login.get(0) + " " + user_login.get(1));
		// myLog("false "+false_user_login.get(0) + " " +
		// false_user_login.get(1));
		final Dialog dlg = new Dialog("Ваш аккаунт в ЖЖ");
		// dlg.setUIID("Dialog");
		dlg.setLayout(new BorderLayout());
		dlg.getContentPane().setUIID("DialogContentPane");
		// dlg.setUIID("CustomDialog");
		TextField.setUseNativeTextInput(true);
		final TextField login = new TextField();
		login.setHint("Логин");
		// login.setAlignment(TextArea.RIGHT);
		final TextField pass = new TextField();
		pass.setHint("Пароль");
		// pass.setAlignment(TextArea.RIGHT);
		if (user_login != null && user_login.get(0).length() > 1) {
			login.setText(user_login.get(0));
			pass.setText(user_login.get(1));
		}

		pass.setConstraint(TextField.PASSWORD);
		Container by = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		by.setUIID("DialogContentPane");
		by.addComponent(login);
		by.addComponent(pass);
		dlg.addComponent(BorderLayout.CENTER, by);
		Container bc = new Container(new FlowLayout(Component.CENTER));
		bc.setUIID("DialogCommandArea");

		Button ok = new Button(new Command("ОК") {
			public void actionPerformed(ActionEvent ev) {
				isRegistered = false;
				dlg.dispose();

				if (login.getText().length() > 0 && pass.getText().length() > 0) {
					user_login = new Vector<String>();
					user_login.add(login.getText());
					user_login.add(pass.getText());
					Storage.getInstance().writeObject("user_login", user_login);
					// dlg.dispose();
					myLog("user: " + user_login.get(0) + " " + user_login.get(1));
					if (user_login != null && user_login.size() > 1 && user_login.get(0).trim().length() > 0
							&& user_login.get(1).trim().length() > 0) {

						authorizeUser();

						if (!isRegistered) {
							if (isNetworkError) {
								Dialog.show("Ошибка", "Не удалось получить данные с сервера.", "   OK   ", null);
							} else {
								Dialog.show("Ошибка", "Неправильный логин или пароль", "OK", null);
								login();
							}
						} else {
							showFriends(communitiesForm);
						}
					} else {
						if (isNetworkError) {
							Dialog.show("Ошибка", "Не удалось получить данные с сервера.", "   OK   ", null);
						} else {
							Dialog.show("Ошибка", "Неправильный логин или пароль", "OK", null);
							login();
						}
					}

				} else {
					login();
				}
			}
		});
		ok.setUIID("DialogButtonCommand");

		Button cancel = new Button(new Command("Отмена") {
			public void actionPerformed(ActionEvent ev) {
				dlg.dispose();
			}
		});
		cancel.setUIID("DialogButtonCommand");
		bc.addComponent(ok);
		bc.addComponent(cancel);
		ok.setPreferredW(dlg.getDialogComponent().getPreferredW() / 3);
		cancel.setPreferredW(dlg.getDialogComponent().getPreferredW() / 3);
		// Container fc = new Container(new FlowLayout(Component.CENTER));
		// fc.addComponent(bc);
		dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
		dlg.revalidate();
		dlg.showPacked(BorderLayout.CENTER, true);
	}

	private void setTopCommands(final Container c) {
		final MForm f = (MForm) c.getComponentForm();
		f.removeAllCustomCommands();

		Command friends = new Command("Друзья", theme.getImage("1_friends.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				isUsers = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showFriends(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Обновить", theme.getImage("1_navigation_refresh.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				isUsers = false;

				communitiesForm.show();

				c.removeAll();
				showItems(c, new ArrayList<MyItem>());
				// showItems(c, itemsListAll.get(selectedURL) == null ? new
				// ArrayList<MyItem>() : itemsListAll.get(selectedURL));
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(topUsers);

		Command communities = new Command("Сообщества", theme.getImage("6_social_person.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				isUsers = false;
				showCommunities(f);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};

		f.addCustomCommand(communities);
	}

	private void showTop(final MForm f) {
		// itemsSkipNum = 10;
		totalEntr = 10000;
		isUsers = false;
		isTags = false;
		isFriends = false;
		selectedTag = "";
		selectedItem = "";
		savedItemId = null;

		selectedTagItems = 10000;
		lj_journal_name = "top";
		m_lj_journal_name = "top";
		selectedURL = "http://m.livejournal.com/ratings/posts/visitors/";
		if (communitiesForm == null)
			communitiesForm = newMyForm("Топ", null);
		final Container c = addLayoutY(communitiesForm);

		setTopCommands(c);
		// deAuthorizeUser();
		// Cookie.clearCookiesFromStorage();
		// isRegistered = false;
		// System.out.println("топ");
		showItems(c, itemsListAll.get(selectedURL) == null ? new ArrayList<MyItem>() : itemsListAll.get(selectedURL));
		communitiesForm.setCustomTitle("Топ");

	}

	private void showItems(final Container c, final ArrayList<MyItem> itemsList) {

		myLog("showItems");
		// c.removeAll();

		if (selectedTag.length() > 0)
			((MForm) c.getComponentForm()).setCustomTitle(
					(nameConv.containsKey(lj_journal_name) ? nameConv.get(lj_journal_name) : lj_journal_name) + " - "
							+ selectedTag);
		else
			((MForm) c.getComponentForm()).setCustomTitle(
					(nameConv.containsKey(lj_journal_name) ? nameConv.get(lj_journal_name) : lj_journal_name));

		// hasTopEntry = false;
		isBusy = true;
		isFilterAdded = false;

		if ("top".equals(lj_journal_name)) {
			isFriends = false;
			setTopCommands(c);
		} else if ("friends".equals(lj_journal_name)) {
			isTop = false;
			setFriendsCommands(c);
		}

		// final int[] p = { 1 };
		final int[] fr = { 1 };

		myLog(lj_journal_name);
		if (itemsList.size() > 0) {
			// if ("top".equals(lj_journal_name)) {
			// p[0] = (int) Math.floor((double) itemsList.size() / 10) + 1;
			// myLog("Page: " + p[0]);
			// } else {
			fr[0] = (int) Math.floor((double) itemsList.size() / 10) + 1;
			myLog("Page: " + fr[0]);
			// }
		}
		// myLog(user_login.get(0) + " " + user_login.get(1));

		InfiniteScrollAdapter.createInfiniteScroll(c, new Runnable() {
			private int itemsCounter = 0;
			ArrayList<String> itemsListIds = new ArrayList<String>();

			public void addCmp() {
				myLog(itemsList.size() + " " + itemsCounter + " " + selectedTagItems + " " + itemsSkipNum + " "
						+ totalEntr);
				if (itemsList.size() > 0) {
					if ((itemsList.size() - itemsCounter > 0 && itemsList.size() <= selectedTagItems)
							|| (itemsList.size() > 0 && itemsCounter == 0)) {
						Component[] buttons = new Component[itemsList.size() - itemsCounter];

						for (int i = itemsCounter; i < itemsList.size(); i++) {

							final MyItem entry = itemsList.get(i);
							itemsListIds.add(entry.getId());

							MultiButton mb = new MultiButton();
							mb.setEmblem(null);
							mb.setHorizontalLayout(false);

							mb.setTextLine1(entry.getName());
							if (entry.getAuthor() != null && entry.getAuthor().length() > 0
									&& !entry.getAuthor().equals(lj_journal_name))
								mb.setTextLine2((!isUsers ? entry.getAuthor() : "")
										+ (entry.getCommunity().length() > 0
												&& !entry.getCommunity().equals(m_lj_journal_name)
														? "(" + entry.getCommunity() + ")" : "")
										+ (entry.getDate().length() > 0 && entry.getAuthor().length() > 0 && !isUsers
												? " - " : "")
										+ entry.getDate());
							else
								mb.setTextLine2(entry.getDate());

							final MyItem e = entry;
							mb.setCommand(new Command("") {
								public void actionPerformed(ActionEvent ev) {

									// selectedURL = e.getURL();
									selectedID = e.getId();
									selectedItem = e.getName();
									// clickedItem = mb;
									// showForm("Browser", null);
									currentMyItem = entry;
									showBrowserForm((MForm) c.getComponentForm());
								}
							});

							// myLog(entry.getName() + " "
							// + (i - itemsCounter));
							buttons[i - itemsCounter] = mb;
							if (entry.getId() == selectedID)
								clickedItem = mb;

						}
						// myLog(""+(itemsList.size()-((int) Math.floor((double)
						// itemsList.size() / 10))*10));
						// !(itemsList.size()-((int) Math.floor((double)
						// itemsList.size() / 10))*10>0)
						if (itemsList.size() - itemsCounter >= itemsSkipNum && itemsList.size() < totalEntr) {
							InfiniteScrollAdapter.addMoreComponents(c, buttons, true);
						} else {
							InfiniteScrollAdapter.addMoreComponents(c, buttons, false);
							Label lb = new Label();
							lb.setText("Всего записей: " + itemsList.size());
							c.addComponent(lb);
							totalEntr = itemsList.size();
						}

						itemsCounter += itemsList.size() - itemsCounter;

					} else {
						Component[] label = new Component[1];
						Label lb = new Label();
						lb.setText("Всего записей: " + itemsList.size());
						totalEntr = itemsList.size();
						label[0] = lb;
						InfiniteScrollAdapter.addMoreComponents(c, label, false);

					}
				} else {
					// System.out.println("tttt");
					Dialog.show(templateErrorTitle, templateError, "OK", null);

					// back();
				}
				isBusy = false;

				if ("http://m.livejournal.com/read/friends/".equals(selectedURL))
					addFilter();
				c.revalidate();
			}

			// @SuppressWarnings("unchecked")
			public void addFilter() {
				// Container titleArea = ((MForm)
				// c.getComponentForm()).getCustomTitleArea();
				if (!isFilterAdded && friendsFilter != null && friendsFilter.size() > 0) {

					isFilterAdded = true;

					final ComboBox cb = new ComboBox(new Vector<String>(friendsFilter.keySet()));
					// Container fc = new Container(new BorderLayout());

					if (friendsSelectedFilter != null) {
						myLog(friendsSelectedFilter + " " + cb.getSelectedItem());
						if (friendsFilter.keySet().contains(friendsSelectedFilter)) {
							cb.setSelectedItem(friendsSelectedFilter);
						} else {
							friendsSelectedFilter = null;
						}

					}

					cb.addSelectionListener(new SelectionListener() {
						public void selectionChanged(int oldSelected, int newSelected) {
							if (oldSelected != newSelected) {
								myLog(cb.getSelectedItem().toString());
								friendsSelectedFilter = cb.getSelectedItem().toString();
								// Storage.getInstance().writeObject("friends_filter_selected",
								// friendsSelectedFilter);
								c.removeAll();
								showItems(c, new ArrayList<MyItem>());
							}
						}
					});

					// fc.addComponent(BorderLayout.CENTER, cb);
					// titleArea.addComponent(BorderLayout.SOUTH, cb);
					cb.setPreferredH(cb.getPreferredH() - (int) Math.round(((double) cb.getPreferredH() / 100) * 25));
					if (c.getComponentForm().getClientProperty("FilterContainer") != null)
						((Container) c.getComponentForm().getClientProperty("FilterContainer"))
								.addComponent(BorderLayout.NORTH, cb);
					// c.getComponentForm().putClientProperty("FilterBox", cb);

					// myLog(""+((MForm)
					// c.getComponentForm()).getCustomTitleArea().getPreferredH());
					// fc.setPreferredH(cb.getPreferredH()-(int)Math.round(((double)cb.getPreferredH()/100)*10));
					// titleArea.setPreferredH(titleArea.getPreferredH() +
					// cb.getPreferredH());//
					// +fc.getStyle().getMargin(Component.TOP));
					// myLog(""+((MForm)
					// c.getComponentForm()).getCustomTitleArea().getPreferredH());
					((MForm) c.getComponentForm()).revalidate();
				}

			}

			public void run() {

				if (itemsList.size() > 0 && itemsCounter == 0) {
					addCmp();
					if (c.getComponentCount() > 0)
						c.scrollRectToVisible(0, 0, 1, c.getHeight(), c.getComponentAt(0));
					if (clickedItem != null) {
						// myLog(clickedItem);
						c.scrollComponentToVisible(clickedItem);
					} else {
						c.scrollComponentToVisible(c.getComponentAt(0));
					}

					// itemsCounter = itemsList.size();

				} else if (itemsList.size() < selectedTagItems) {

					MyConnectionRequest requestElement = new MyConnectionRequest() {
						String response;

						// InfiniteProgress ip = new InfiniteProgress();
						// Dialog dlg = ip.showInifiniteBlocking();

						// @Override
						// protected String initCookieHeader(String cookie) {
						// myLog("cookie=" + cookie);
						//
						// if (!isRegistered) {
						// String cookie2 = null;
						// if (cookie != null && !(cookie.indexOf("langpref=") >
						// 0))
						// cookie2 =
						// "rating_show_custom=1; langpref=ru/1385193547; " +
						// cookie;
						// if (cookie == null)
						// cookie2 =
						// "rating_show_custom=1; langpref=ru/1385193547";
						//
						// return cookie2;
						// }else{
						// return cookie;
						// }
						//
						// }

						@Override
						protected void postResponse() {
							myLog("Register postResponse showItems");
							// dlg.dispose();
							int i = 0;
							RE pattern;
							if ("http://m.livejournal.com/read/friends/".equals(selectedURL) && !isFilterAdded) {
								pattern = new RE("<select.*?name=\"filter\">(.*?)</select>");
								if (pattern.match(response)) {
									friendsFilter = new LinkedHashMap<String, String>();
									RE patternO = new RE("<option value=\"(.*?)\".*?>(.*?)</option>");
									while (patternO.match(pattern.getParen(1), i)) {
										friendsFilter.put(patternO.getParen(2), patternO.getParen(1));
										i = patternO.getParenEnd(0);
									}
									// Storage.getInstance().writeObject("friends_filter",
									// friendsFilter);
								}
								// myLog(friendsFilter.toString());
							}

							if ("top".equals(lj_journal_name)) {
								m_user = lj_communities_list.get("top");
							} else {
								m_user = lj_communities_list.get("friends");
							}
							// log (lj_journal_name);
							// log (lj_user.get("item_start"));
							pattern = new RE(m_user.get("item_start"));
							RE patternH = new RE("(<.*?>)|(&[^;]+?;)");
							RE patternMeta = new RE("<p class=\"item-meta\">(.*?)</p>");
							i = 0;
							// int j = 0;
							String nam;
							// .*?<h3
							// class=\"entry-header\">(.*?)</h3>.*?<a
							// class=\"permalink\" href=\"(.*?)\">

							// myLog(response);
							while (pattern.match(response, i)) {
								// j++;
								i = pattern.getParenStart(0);
								MyItem item = new MyItem();
								RE pattern2 = new RE(m_user.get("item_date"));
								try {
									if (m_user.get("item_date").length() > 0) {
										if (pattern2.match(response, i)) {
											item.setDate(patternH.subst(pattern2.getParen(1), "").trim());
										}
									} else {
										item.setDate("");
									}
									// myLog(item.getDate());
									item.setName("Без темы");
									pattern2 = new RE(m_user.get("item_name"));
									if (pattern2.match(response, i)) {

										if (pattern2.getParen(1).length() > 0) {
											// if (pattern2.getParen(1)
											// .indexOf(topEntry) == -1) {
											nam = patternH.subst(pattern2.getParen(1), "").trim();
											// myLog(nam);
											if (nam.length() > 0)
												item.setName(
														nam.indexOf("- ") == 0 ? nam.substring(2).trim() : nam.trim());
											else
												item.setName("Без темы");

											// } else {
											// hasTopEntry = true;
											// }
										}
									}
									// myLog(item.getName());
									pattern2 = new RE(m_user.get("item_id"));
									if (pattern2.match(response, i)) {
										item.setId(pattern2.getParen(1));
									}
									// if (!isUsers) {
									if (!"top".equals(lj_journal_name)) {
										if (patternMeta.match(response, i)) {

											pattern2 = new RE(m_user.get("item_author"));
											if (pattern2.match(patternMeta.getParen(1))) {
												item.setAuthor(patternH.subst(pattern2.getParen(1), "").trim());
												if (pattern2.match(patternMeta.getParen(1), pattern2.getParenEnd(0))) {
													item.setCommunity(patternH.subst(pattern2.getParen(1), "").trim());
												}
											}
											i = patternMeta.getParenEnd(0);
										} else {
											i = pattern2.getParenEnd(0);
										}

									} else {
										pattern2 = new RE(m_user.get("item_author"));
										if (pattern2.match(response, i)) {
											item.setAuthor(patternH.subst(pattern2.getParen(1), "").trim());
										}
										i = pattern2.getParenEnd(0);
									}

									// } else {
									// item.setAuthor("");
									// }
									// myLog(item.getAuthor());
									// item.setURL(pattern2.getParen(1)
									// + pattern2.getParen(2)
									// + ".html");
									if (item.getId().length() > 0 && !itemsListIds.contains(item.getId()))
										itemsList.add(item);
									// myLog(item.getId() + " " + item.getName()
									// + " " + item.getDate() + " " +
									// item.getAuthor());
									if (i < 0)
										break;

								} catch (Exception ex) {
									ex.printStackTrace();
									break;
								} catch (Error er) {
									er.printStackTrace();
									break;
								}
							}

							itemsListAll.put(getMyURL() + getTag(), itemsList);
							myLog(getMyURL() + " " + selectedURL + " " + getTag() + " " + selectedTag);
							if (getMyURL() == selectedURL && getTag() == selectedTag) {
								if (itemsSkipNum == 0)
									itemsSkipNum = getSkipNum();
								if (selectedTag.length() > 0 || (selectedTag.length() <= 0 && !isTags))
									addCmp();
							}

						}

						protected void readResponse(InputStream input) throws IOException {

							response = Util.readToString(input, "UTF-8");
							Util.cleanup(input);
							RE pattern = new RE("\r\n");
							response = pattern.subst(response, "", RE.REPLACE_ALL);
							pattern = new RE("\n|\r");
							response = pattern.subst(response, "", RE.REPLACE_ALL);
							pattern = new RE("\\s+");
							response = pattern.subst(response, " ", RE.REPLACE_ALL);

							// RE pattern = new RE(
							// "<h2 class=\"date-header\">(.*?)</h2><h3
							// class=\"entry-header\">(.*?)</h3>");

							// if (isUsers) {
							// if (selectedTag.length() > 0 &&
							// lj_users_list_tag.containsKey(lj_journal_name))
							// lj_user = lj_users_list_tag.get(lj_journal_name);
							// else
							// lj_user = lj_users_list.get(lj_journal_name);
							// } else {
							// if (selectedTag.length() > 0 &&
							// lj_communities_list_tag.containsKey(lj_journal_name))
							// lj_user =
							// lj_communities_list_tag.get(lj_journal_name);
							// else
							// lj_user =
							// lj_communities_list.get(lj_journal_name);
							// }

							// if (getSkipNum() == 0)
							// setSkipNum(j);

						}
					};

					// if (authCookie == null) {
					// authorizeUser();
					// }
					//
					// if (authCookie != null) {

					// requestElement.addRequestHeader("cookie", "");

					// if (authCookie != null)
					// , "ljsession=" + authCookie +
					// "; rating_show_custom=1; langpref=ru/1384193547");
					// else

					// ljuniq=VAwhuVnJ4kFYysy%3A1315117848%3Apgstats0;
					// ljdomsess.m=v1:u32491662:s270:t1384876800:g48fae2485d74b8232af336cdac6f7e39e5d089f8//1;
					// rating_show_custom=1; langpref=ru/1384193547;

					// requestElement.removeAllArguments();

					if (!isRegistered) {
						requestElement.removeAllArguments();
						requestElement.addRequestHeader("cookie", "rating_show_custom=1; langpref=ru/1469450983;");
					}

					requestElement.setPost(false);
					requestElement.setTag(selectedTag);

					// myLog(selectedURL);
					String rUrl = new String(selectedURL);
					requestElement.setUrl(selectedURL);
					requestElement.setMyURL(selectedURL);

					if ("http://m.livejournal.com/read/friends/".equals(selectedURL)) {
						if (friendsSelectedFilter != null && friendsFilter != null) {
							myLog("***" + friendsSelectedFilter + "**" + friendsFilter.get(friendsSelectedFilter));
							requestElement.addArgument("filter", friendsFilter.get(friendsSelectedFilter));
						} else {
							requestElement.addArgument("filter", "0");
						}
					}

					if (selectedTag.length() > 0) {
						// if ("top".equals(lj_journal_name) ||
						// "friends".equals(lj_journal_name)) {
						// rUrl = "http://m.livejournal.com/read/user/" +
						// currentMyItem.getAuthor() + "/tag/" +
						// Util.encodeUrl(selectedTag) + "/";
						// } else {
						rUrl = rUrl + "tag/" + Util.encodeUrl(selectedTag) + "/";// requestElement.addArgument("tag",
																					// selectedTag);
																					// }
					}

					// if ("top".equals(lj_journal_name)) {
					// // myLog(selectedURL + p[0]);
					// requestElement.setUrl(selectedURL + "p" + p[0]);
					// p[0]++;
					// // } else if ("friends".equals(lj_journal_name)) {
					// } else {
					// myLog(rUrl + fr[0]);
					requestElement.setUrl(rUrl + "p" + fr[0]);
					fr[0]++;
					// }

					myLog(requestElement.getUrl());
					// myLog(selectedURL);

					// if (selectedTag.length() > 0)
					// requestElement.addArgument("tag", selectedTag);
					// if (itemsCounter > 0 && !"top".equals(lj_journal_name) &&
					// !"friends".equals(lj_journal_name)) {
					// requestElement.addArgument("skip", "" + (hasTopEntry ?
					// itemsCounter + 1 : itemsCounter));
					// }
					// isBusy = true;
					isNetworkError = false;
					NetworkManager.getInstance().addToQueue(requestElement);
					// runSingleRequest(requestElement);
					// }

				} else {
					addCmp();
				}
			}
		});
		c.getComponentForm().revalidate();
	}

	// protected void showSettingsForm(final MyForm parent) {
	//
	// MyForm f = newMyForm("Настройки", parent);
	// //Container c = addLayoutY(f);
	//
	// Command back = new Command(parent.getTitle()) {
	// public void actionPerformed(ActionEvent ev) {
	// parent.showBack();
	// }
	// };
	// myLog("showItemsForm " + selectedTag);
	// f.setBackCommand(back);
	// f.revalidate();
	// f.show();
	//
	// // showItems(c, itemsListAll.get(selectedURL + selectedTag) == null ?
	// // new ArrayList<MyItem>() : itemsListAll.get(selectedURL +
	// // selectedTag));
	//
	// }
	protected void resetUser() {
		totalEntr = 10000;
		selectedTag = "";
		selectedItem = "";
		savedItemId = null;
		selectedTagItems = 10000;

		if (isTop) {
			isUsers = false;
			isTags = false;
			isFriends = false;
			lj_journal_name = "top";
			m_lj_journal_name = "top";
			selectedURL = "http://m.livejournal.com/ratings/posts/visitors/";
		} else if (isFriends) {
			isUsers = false;
			isTop = false;
			isTags = false;
			lj_journal_name = "friends";
			m_lj_journal_name = "friends";
			selectedURL = "http://m.livejournal.com/read/friends/";
		} else if (isUsers) {
			// showUsers(communitiesForm);
		} else {
			// showCommunities(communitiesForm);
		}
	}

	protected void showItemsForm(final MForm parent) {
		myLog("showItemsForm");
		selectedTagItems = 10000;
		totalEntr = 10000;

		if (itemsForm == null) {
			itemsForm = newMyForm("Записи", parent);
		}
		if (parent != null) {
			Command back = new Command(parent.getTitle()) {
				public void actionPerformed(ActionEvent ev) {

					if (parent.equals(browserForm)) {
						if (browserForm.getClientProperty("myWb") != null) {
							parent.showBack();
							parent.revalidate();
						} else {
							// resetUser();
							communitiesForm.showBack();
						}

					} else if (parent.equals(itemsForm)) {
						// resetUser();
						if (isTags) {
							showTagsForm(itemsForm);
							// if (tagsForm != null &&
							// tagsForm.getClientProperty("mainc") != null)
							// showTags((Container)
							// tagsForm.getClientProperty("mainc"),
							// tagsListAll.get(selectedURL) == null ? new
							// ArrayList<SimpleEntry<String, Integer>>()
							// : tagsListAll.get(selectedURL));
							// else if (itemsForm.getClientProperty("mainc") !=
							// null)
							// showTags((Container)
							// itemsForm.getClientProperty("mainc"),
							// tagsListAll.get(selectedURL) == null ? new
							// ArrayList<SimpleEntry<String, Integer>>()
							// : tagsListAll.get(selectedURL));
						} else {
							communitiesForm.showBack();
						}
					} else {

						parent.showBack();
					}
				}
			};
			// myLog("showItemsForm " + selectedTag);
			itemsForm.setBackCommand(back);
		} else {
			Command back = new Command("") {
				public void actionPerformed(ActionEvent ev) {
					// resetUser();
					if (isTags) {
						showTagsForm(itemsForm);
						// if (tagsForm != null &&
						// tagsForm.getClientProperty("mainc") != null)
						// showTags((Container)
						// tagsForm.getClientProperty("mainc"),
						// tagsListAll.get(selectedURL) == null ? new
						// ArrayList<SimpleEntry<String, Integer>>()
						// : tagsListAll.get(selectedURL));
						// else if (itemsForm.getClientProperty("mainc") !=
						// null)
						// showTags((Container)
						// itemsForm.getClientProperty("mainc"),
						// tagsListAll.get(selectedURL) == null ? new
						// ArrayList<SimpleEntry<String, Integer>>()
						// : tagsListAll.get(selectedURL));
					} else {
						communitiesForm.showBack();
					}
				}
			};
			// myLog("showItemsForm " + selectedTag);
			itemsForm.setBackCommand(back);
		}
		final Container c = addLayoutY(itemsForm);
		itemsForm.putClientProperty("mainc", c);
		// itemsForm.removeAllCommands();
		itemsForm.removeAllCustomCommands();

		Command tags = new Command("Теги", theme.getImage("4_collections_labels.png")) {
			public void actionPerformed(ActionEvent ev) {
				if (!isBusy) {
					// tagsSortA=false;
					// isBusy = true;
					isTags = true;
					showTagsForm(itemsForm);
					// if ((Container)
					// tagsForm.getClientProperty("mainc")!=null)
					// showTags((Container) tagsForm.getClientProperty("mainc"),
					// tagsListAll.get(selectedURL) == null ? new
					// ArrayList<SimpleEntry<String, Integer>>() :
					// tagsListAll.get(selectedURL));
				}
			}
		};

		// tags.putClientProperty("TitleCommand", Boolean.TRUE);
		itemsForm.addCustomCommand(tags);

		Command reload = new Command("Обновить", theme.getImage("1_navigation_refresh.png")) {
			public void actionPerformed(ActionEvent ev) {
				if (!isBusy) {
					c.removeAll();
					showItems(c, new ArrayList<MyItem>());
					if (c.getComponentCount() > 0)
						c.scrollRectToVisible(0, 0, 1, c.getHeight(), c.getComponentAt(0));
				}
			}
		};

		// reload.putClientProperty("TitleCommand", Boolean.TRUE);
		itemsForm.addCustomCommand(reload);

		itemsForm.revalidate();
		itemsForm.show();

		addMainCommands(itemsForm);

		showItems(c, itemsListAll.get(selectedURL + selectedTag) == null ? new ArrayList<MyItem>()
				: itemsListAll.get(selectedURL + selectedTag));

	}

	private void addBrowserCommands(final MForm f) {

		final WebBrowser myWb = (WebBrowser) f.getClientProperty("myWb");

		f.removeAllCustomCommands();

		if (savedItemId == null) {
			Command save = new Command("Сохранить", theme.getImage("5_content_save.png")) {
				public void actionPerformed(ActionEvent ev) {
					if (saveEnabled) {
						myLog(selectedURL);

						// Thread t = new Thread() {
						// public void run() {

						// savePage(wbPageOriginal,
						// (("top".equals(lj_journal_name) ||
						// "friends".equals(lj_journal_name)) ?
						// currentMyItem.getAuthor() : lj_journal_name) + "."
						// + selectedID, currentMyItem);
						savePage(wbPageOriginal, lj_journal_name + "."
								+ (("top".equals(lj_journal_name) || "friends".equals(lj_journal_name))
										? (currentMyItem.getCommunity().length() > 0 ? currentMyItem.getCommunity()
												: currentMyItem.getAuthor()) + "."
										: "")
								+ selectedID, currentMyItem);
						f.revalidate();
						// }
						// };
						// t.run();

						// Dialog.show("", "Страница сохранена.", "OK", null);
					}

				}
			};

			// save.putClientProperty("TitleCommand", Boolean.TRUE);
			f.addCustomCommand(save);
		}

		if (BrowserComponent.isNativeBrowserSupported()) {
			if (isPinchToZoom) {
				Command com = new Command("Уменьшение", theme.getImage("9_av_return_from_full_screen.png")) {
					public void actionPerformed(ActionEvent ev) {
						isPinchToZoom = false;
						((BrowserComponent) myWb.getInternal()).setPinchToZoomEnabled(false);
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + wbPage + htmlFooter, null);
						// ((BrowserComponent) myWb.getInternal()).reload();
						addBrowserCommands(f);

					}
				};

				// com.putClientProperty("TitleCommand", Boolean.TRUE);
				f.addCustomCommand(com);
			} else {

				Command com = new Command("Увеличение", theme.getImage("9_av_full_screen.png")) {
					public void actionPerformed(ActionEvent ev) {
						isPinchToZoom = true;
						((BrowserComponent) myWb.getInternal()).setPinchToZoomEnabled(true);
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + wbPageOriginal + htmlFooter,
								null);
						// ((BrowserComponent) myWb.getInternal()).reload();
						addBrowserCommands(f);

					}
				};

				// com.putClientProperty("TitleCommand", Boolean.TRUE);
				f.addCustomCommand(com);
			}
		}

		Command com = new Command("Комментарии", theme.getImage("6_social_chat.png")) {
			public void actionPerformed(ActionEvent ev) {
				// Display.getInstance().execute(
				// myWb.setPage((isNight ? darkHtmlHeader : lightHtmlHeader) +
				// "Загрузка..." + htmlFooter, null);
				// InfiniteProgress ip = new InfiniteProgress();
				// Dialog dlg = ip.showInifiniteBlocking();
				// if (BrowserComponent.isNativeBrowserSupported())
				// ((BrowserComponent) myWb.getInternal()).clearHistory();
				// myWb.setURL(
				Display.getInstance()
						.execute(
								"http://m.livejournal.com/read/user/"
										+ ("top".equals(lj_journal_name) || "friends".equals(lj_journal_name)
												? (currentMyItem.getCommunity().length() > 0
														? currentMyItem.getCommunity() : currentMyItem.getAuthor())
												: lj_journal_name)
										+ "/" + currentMyItem.getId() + "/comments#comments");
				// (savedItemId != null ? savedItemId : selectedID)
			}
		};

		// com.putClientProperty("TitleCommand", Boolean.TRUE);
		f.addCustomCommand(com);
		f.revalidate();
	}

	protected void showBrowserPage(MForm f) {

		// f.revalidate();
		f.removeAllShowListeners();

		final WebBrowser myWb = (WebBrowser) f.getClientProperty("myWb");

		if (savedItemId == null) {
			InfiniteProgress ip = new InfiniteProgress();
			final Dialog dlg = ip.showInifiniteBlocking();

			// findMyWebBrowser(f).setURL(selectedURL);
			MyConnectionRequest requestElement = new MyConnectionRequest() {

				// InfiniteProgress ip = new InfiniteProgress();
				// Dialog dlg = ip.showInifiniteBlocking();

				String resp = "";

				@Override
				protected void postResponse() {
					// myLog("Register postResponse");
					// myLog(resp);
					wbPage = "<b>" + selectedItem + "</b><br><br>" + resp;
					// Display.getInstance().setProperty("WebLoadingHidden",
					// "true");
					if (myWb != null) {
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + wbPage + htmlFooter, null);

					}
					saveEnabled = true;
					if (dlg != null)
						dlg.dispose();
					// f.revalidate();
				}

				protected void readResponse(InputStream input) throws IOException {

					// resp="";
					String tagStr = "";
					String[] tagStrArr = null;
					String response = Util.readToString(input, "UTF-8");
					// myLog(Util.decode(response, "UTF-8", false));
					Util.cleanup(input);
					String[] responseArr = Util.split(response, "\n");

					resp = "";
					wbPageOriginal = "";

					for (int i = 0; i < responseArr.length; i++) {
						if ("events_1_event".equals(responseArr[i]) && resp.length() == 0) {
							resp = Util.decode(responseArr[i + 1], "UTF-8", false);
							// myLog(resp);
						} else if ("taglist".equals(responseArr[i]) && tagStrArr == null) {
							if ((i + 2) < responseArr.length && responseArr[i + 2].length() > 0) {
								tagStrArr = Util.split(responseArr[i + 2], ",");
							}

						}
					}

					if (tagStrArr != null) {
						for (int i = 0; i < tagStrArr.length; i++) {
							if (tagStrArr[i].trim().indexOf("\"") >= 0)
								tagStr = tagStr + "<a href='http://tag/#" + tagStrArr[i].trim() + "'>"
										+ StringUtil.replaceAll(tagStrArr[i].trim(), " ", "&nbsp;") + "</a>";
							else
								tagStr = tagStr + "<a href=\"http://tag/#" + tagStrArr[i].trim() + "\">"
										+ StringUtil.replaceAll(tagStrArr[i].trim(), " ", "&nbsp;") + "</a>";
							if (i < tagStrArr.length - 1)
								tagStr = tagStr + ", &nbsp;&nbsp;&nbsp;";
						}
					}

					// resp = Util.decode(responseArr[3], "UTF-8", false);
					if (resp != null && resp.length() > 0) {

						resp = formatHTML(resp);

						wbPageOriginal = "<b>" + selectedItem + "</b><br><br>" + resp
								+ ((tagStr.length() > 0)
										? "<br><br><div style=\"line-height: 2\">Теги: " + tagStr + "</div><br><br>"
										: "");

						resp = adaptHTML(resp) + ((tagStr.length() > 0)
								? "<br><br><div style=\"line-height: 2\">Теги: " + tagStr + "</div><br><br>" : "");
					}

					// myLog(Util.decode(response, "UTF-8", false));
					// myLog(resp);

				}
			};

			requestElement.setUrl(lj_flat_url);

			requestElement.removeAllArguments();
			requestElement.setPost(true);

			requestElement.addArgument("mode", "getevents");
			if (isRegistered) {
				requestElement.addArgument("user", user_login.get(0));
				requestElement.addArgument("password", user_login.get(1));
			} else {
				requestElement.addArgument("user", lj_login);
				requestElement.addArgument("password", lj_password);
			}
			// if ("top".equals(lj_journal_name) ||
			// "friends".equals(lj_journal_name)) {

			if (currentMyItem.getCommunity().length() > 0)
				requestElement.addArgument("usejournal", currentMyItem.getCommunity());
			else if (currentMyItem.getAuthor().length() > 0)
				requestElement.addArgument("usejournal", currentMyItem.getAuthor());
			else
				requestElement.addArgument("usejournal", lj_journal_name);

			requestElement.addArgument("selecttype", "one");
			requestElement.addArgument("ver", "1");
			requestElement.addArgument("ditemid", selectedID);
			// requestElement.setSilentRetryCount(3);
			isNetworkError = false;
			NetworkManager.getInstance().addToQueueAndWait(requestElement);
		} else {

			// dlg.showPacked(BorderLayout.CENTER, false);

			// WebBrowser myWb = new WebBrowser(){
			//
			// @Override
			// public void onLoad(String url){
			// if (Display.getInstance().getCurrent() instanceof Dialog)
			// ((Dialog) Display.getInstance().getCurrent()).dispose();
			// }
			// };
			//
			//
			// // myLog(myWb.getUIID());
			// if (BrowserComponent.isNativeBrowserSupported())
			// ((BrowserComponent)
			// myWb.getInternal()).setPinchToZoomEnabled(isPinchToZoom);
			//
			// f.addComponent(BorderLayout.CENTER, myWb);
			// myWb.setPage((isNight ? darkHtmlHeader : lightHtmlHeader) + " " +
			// htmlFooter, null);
			// f.putClientProperty("MyWeb", myWb);
			//
			// if (BrowserComponent.isNativeBrowserSupported()) {
			// ((BrowserComponent)
			// myWb.getInternal()).setBrowserNavigationCallback(new
			// BrowserNavigationCallback() {
			// public boolean shouldNavigate(String url) {
			// // myLog("URL Clicked: " + url);
			// Display.getInstance().execute(url);
			// return false;
			// }
			// });
			// }

			// addBrowserCommands(f);
			// if (f.getClientProperty("MyWeb") != null)
			// f.removeComponent((Component) f.getClientProperty("MyWeb"));
			//
			// WebBrowser myWb = new WebBrowser(){
			//
			// @Override
			// public void onLoad(String url){
			// if (Display.getInstance().getCurrent() instanceof Dialog)
			// ((Dialog) Display.getInstance().getCurrent()).dispose();
			// }
			// };
			//
			// if (BrowserComponent.isNativeBrowserSupported())
			// ((BrowserComponent)
			// myWb.getInternal()).setPinchToZoomEnabled(isPinchToZoom);
			//
			// myWb.setPage((isNight ? darkHtmlHeader : lightHtmlHeader) + " " +
			// htmlFooter, null);
			// f.addComponent(BorderLayout.CENTER, myWb);
			//
			// f.putClientProperty("MyWeb", myWb);
			//
			// if (BrowserComponent.isNativeBrowserSupported()) {
			// ((BrowserComponent)
			// myWb.getInternal()).setBrowserNavigationCallback(new
			// BrowserNavigationCallback() {
			// public boolean shouldNavigate(String url) {
			// // myLog("URL Clicked: " + url);
			// Display.getInstance().execute(url);
			// return false;
			// }
			// });
			// }
			//
			//
			// addBrowserCommands(f);
			//
			// f.revalidate();
			// final Dialog dlg = new DiamyLog("Загрузка");
			// dlg.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
			// //dlg.setUIID("CustomDialog");
			// Slider slider = new Slider();
			// slider.setInfinite(true);
			// slider.setEditable(false);
			// dlg.addComponent(slider);
			// dlg.setTimeout(20000);
			// dlg.revalidate();
			// dlg.showPacked(BorderLayout.CENTER, false);

			// Thread t = new Thread() {
			// public void run() {

			// try {
			// Thread.sleep(3000);
			// } catch (InterruptedException e1) {
			//
			// e1.printStackTrace();
			// }
			InfiniteProgress ip = new InfiniteProgress();
			final Dialog dlg = ip.showInifiniteBlocking();
			try {

				InputStream is = Storage.getInstance().createInputStream(lj_journal_name + "." + savedItemId);
				wbPageOriginal = Util.readToString(is);
				Util.cleanup(is);
				if (myWb != null)
					if (wbPageOriginal != null) {
						wbPage = adaptHTML(wbPageOriginal);
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + wbPage + htmlFooter, null);

					} else {
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + "Не удалось открыть страницу."
								+ htmlFooter, null);
					}
				if (dlg != null)
					dlg.dispose();
				// myWb.getStyle().notifyAll();
				// myWb.getStyle().setOpacity(255);
				// myWb.getStyle().setBgTransparency(255);
				// myWb.refreshTheme();
				// Display.getInstance().getCurrent().animateLayout(300);
				// addBrowserCommands(f);
				// f.revalidate();
				// f.repaint();
			} catch (IOException e) {

				wbPageOriginal = "";
				wbPage = "";
				myLog("IOException");
				e.printStackTrace();

				if (dlg != null)
					dlg.dispose();

				Dialog.show("Ошибка", "Файл не найден.", "   OK   ", null);
			}
			// }
			// };
			// t.run();

		}
		firstPage = myWb.getPage();
		// browserForm.setGlassPane(new Painter() {
		// public void paint(Graphics g, Rectangle rect) {
		// g.setAlpha(0);
		// g.setColor(0);
		// g.fillRect(0, 0, browserForm.getWidth(), browserForm.getHeight());
		// //ui.paintComponent(g);
		// //g.setAlpha(255);
		// //g.translate(-tx, -ty);
		// }
		// });

		// browserForm.setGlassPane(null);
		// Display.getInstance().getCurrent().refreshTheme();
		// f.revalidate();
	}

	protected void showBrowserForm(final MForm parent) {
		// findMyWebBrowser(f).setPage("sdfsdfsdf", "");
		isPinchToZoom = false;
		saveEnabled = false;
		firstPage = null;
		// myLog(selectedURL);
		// myLog(selectedID);
		// f.setTitle(selectedItem);
		// isBrowserShowed = false;

		if (browserForm == null) {
			browserForm = newMyForm("Браузер", parent);
		}

		browserForm.removeAll();

		// if (browserForm.getClientProperty("myWb") != null) {
		// ((WebBrowser) browserForm.getClientProperty("myWb")).stop();
		// ((WebBrowser) browserForm.getClientProperty("myWb")).destroy();
		// browserForm.putClientProperty("myWb", null);
		// }

		// Slider slider = new Slider();
		// slider.setInfinite(true);
		// slider.setEditable(false);
		// browserForm.putClientProperty("slider", slider);
		// Container cent= new Container(new BoxLayout(BoxLayout.Y_AXIS));
		// browserForm.addComponent(BorderLayout.CENTER,cent);
		//
		// cent.addComponent(slider);

		// myWb = new WebBrowser(){
		//
		// @Override
		// public void onLoad(String url){
		// // if (browserForm!=null &&
		// browserForm.getClientProperty("slider")!=null)
		// //
		// browserForm.removeComponent((Component)browserForm.getClientProperty("slider"));
		// //browserForm.revalidate();
		// if (Display.getInstance().getCurrent() instanceof Dialog)
		// ((Dialog) Display.getInstance().getCurrent()).dispose();
		// }
		// };

		// if ("top".equals(lj_journal_name) ||
		// "friends".equals(lj_journal_name)) {
		String title;
		if (currentMyItem.getAuthor().length() > 0) {
			if (currentMyItem.getCommunity().length() > 0)
				title = currentMyItem.getAuthor() + "(" + currentMyItem.getCommunity() + ")";
			else
				title = currentMyItem.getAuthor();
		} else {

			title = lj_journal_name;
		}

		if (isSaved)
			title = title + " - cохраненные";

		browserForm.setCustomTitle(title);

		// f.setLayout(new BorderLayout());

		// nf.removeAllCommands();

		Command back = new Command("Назад") {
			public void actionPerformed(ActionEvent ev) {
				parent.showBack();
				parent.revalidate();

				browserForm.removeAll();
				browserForm.putClientProperty("myWb", null);

				// WebBrowser myWb = (WebBrowser)
				// browserForm.getClientProperty("myWb");
				// if (myWb != null) {
				//
				// if (BrowserComponent.isNativeBrowserSupported()) {
				// BrowserComponent bc = (BrowserComponent) myWb.getInternal();
				// if (bc.hasBack()) {
				// bc.back();
				// // String curP = myWb.getPage();
				// // if (curP == null || curP.length() <= 0) {
				// // myWb.setPage(firstPage, null);
				// // firstPage = null;
				// // }
				//
				// } else if (firstPage != null) {
				// myWb.setPage(firstPage, null);
				// firstPage = null;
				// bc.clearHistory();
				// } else {
				// parent.showBack();
				// parent.revalidate();
				//
				// browserForm.removeAll();
				// browserForm.putClientProperty("myWb", null);
				// }
				// } else {
				// parent.showBack();
				// parent.revalidate();
				//
				// browserForm.removeAll();
				// browserForm.putClientProperty("myWb", null);
				// }
				//
				// // ((WebBrowser)
				// // browserForm.getClientProperty("myWb")).stop();
				// // ((WebBrowser)
				// // browserForm.getClientProperty("myWb")).destroy();
				// // browserForm.putClientProperty("myWb", null);
				// // browserForm.setGlassPane(null);
				//
				// } else {
				// parent.showBack();
				// parent.revalidate();
				// }

			}
		};

		browserForm.setBackCommand(back);

		// myLog(((CustomSideMenuBar)
		// nf.getMenuBar()).getSideCommandCount());

		// ((CustomSideMenuBar) nf.getMenuBar()).removeAllSideCommands();
		//
		// if (((CustomSideMenuBar) nf.getMenuBar()).getSideCommandCount()<=0)
		// addMainCommands(nf);

		browserForm.addShowListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final WebBrowser myWb;
				// myWb = new WebBrowser();
				myWb = new WebBrowser() {
					@Override
					public void onLoad(String url) {
						if (this.getPage() == null || this.getPage().length() <= 0) {
							if (firstPage != null) {
								this.setPage(firstPage, null);
								firstPage = null;
								if (BrowserComponent.isNativeBrowserSupported())
									((BrowserComponent) this.getInternal()).clearHistory();
							}
						}
					}
				};
				browserForm.addComponent(BorderLayout.CENTER, myWb);
				browserForm.putClientProperty("myWb", myWb);

				myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + "Загрузка..." + htmlFooter, null);

				if (BrowserComponent.isNativeBrowserSupported())
					((BrowserComponent) myWb.getInternal()).setPinchToZoomEnabled(isPinchToZoom);

				if (BrowserComponent.isNativeBrowserSupported()) {
					((BrowserComponent) myWb.getInternal())
							.setBrowserNavigationCallback(new BrowserNavigationCallback() {
								public boolean shouldNavigate(String url) {
									myLog("URL Clicked: " + url);
									if (url.startsWith("http://tag/#")) {
										String[] urlParts = Util.split(url, "tag/#");
										if (urlParts[1] != null && urlParts[1].length() > 0) {
											// clickedItem = null;
											// itemsSkipNum = 0;
											// totalEntr = 10000;
											// selectedTagItems=10000;
											if ("top".equals(lj_journal_name) || "friends".equals(lj_journal_name)
													|| isSaved) {

												if (!isSaved) {
													if (currentMyItem.getCommunity().length() > 0)
														lj_journal_name = currentMyItem.getCommunity();
													else
														lj_journal_name = currentMyItem.getAuthor();
													m_lj_journal_name = StringUtil.replaceAll(lj_journal_name, "-",
															"_");
												} else {
													if ("top".equals(lj_journal_name)
															|| "friends".equals(lj_journal_name)) {
														if (currentMyItem.getCommunity().length() > 0)
															lj_journal_name = currentMyItem.getCommunity();
														else
															lj_journal_name = currentMyItem.getAuthor();
														m_lj_journal_name = StringUtil.replaceAll(lj_journal_name, "-",
																"_");
													}

													savedItemId = null;
												}
												selectedURL = "http://m.livejournal.com/read/user/" + m_lj_journal_name
														+ "/";
											}
											selectedTag = Util.decode(urlParts[1], "UTF-8", false);
											myLog(selectedTag);

											showItemsForm(browserForm);
										}

										// if(browserForm.getClientProperty("myWb")!=null)
										// ((WebBrowser)browserForm.getClientProperty("myWb")).reload();
									} else {
										Display.getInstance().execute(url);
										// myWb.setURL(url);
										// myWb.setPage((isNight ?
										// darkHtmlHeader :
										// lightHtmlHeader) + "Загрузка..." +
										// htmlFooter, null);
										// InfiniteProgress ip = new
										// InfiniteProgress();
										// Dialog dlg =
										// ip.showInifiniteBlocking();
										return false;
									}
									return false;
								}
							});
				}
				addBrowserCommands(browserForm);

				// if (((SideMenuBar)
				// browserForm.getMenuBar()).getCommandCount() <= 0)
				addMainCommands(browserForm);

				browserForm.revalidate();
				showBrowserPage(browserForm);

			}
		});
		//

		browserForm.revalidate();

		browserForm.show();

		// Dialog dlg = new Dialog("Загрузка");
		// dlg.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
		// Slider slider = new Slider();
		// slider.setInfinite(true);
		// dlg.addComponent(slider);
		// dlg.revalidate();
		// dlg.setTimeout(10000);

		// dlg.showPacked(BorderLayout.CENTER, false);

		// {
		//
		// @Override
		// protected void onShowCompleted() {
		// //super.onShowCompleted();
		// //if (!isBrowserShowed) {
		// // myLog("showBrowser(this)");
		// // isBrowserShowed = true;
		// this.removeAllShowListeners();
		// showBrowser(this);
		// //}
		// // else{
		// // if (this.getClientProperty("MyWeb") != null)
		// // ((WebBrowser)this.getClientProperty("MyWeb")).repaint();
		// // this.revalidate();
		// // }
		// }
		// };

	}

	private String formatHTML(String resp) {

		RE pattern = new RE("\r\n");
		resp = pattern.subst(resp, "<br>", RE.REPLACE_ALL);
		pattern = new RE("\n|\r");
		resp = pattern.subst(resp, "<br>", RE.REPLACE_ALL);
		pattern = new RE("style=[\"'](.*?)[\"']", RE.MATCH_CASEINDEPENDENT);
		resp = pattern.subst(resp, " ", RE.REPLACE_ALL);
		// pattern = new RE("src=[\"'].*?(http.*?(jpe?g|png|gif)).*?[\"']",
		// RE.MATCH_CASEINDEPENDENT);
		// resp = pattern.subst(resp, "src=\"$1\"", RE.REPLACE_BACKREFERENCES);
		// pattern = new RE("\\(oE1Rf3\\)");
		// resp = pattern.subst(resp, "<br>", RE.REPLACE_ALL);
		// pattern = new RE("^<img.*?src=[\"'](.*?)[\"'].*?>$",
		// RE.MATCH_CASEINDEPENDENT);

		// pattern = new RE("<img[^>]+src=[\"']([^\"'>]+)[\"'][^>]+>",
		// RE.MATCH_CASEINDEPENDENT);
		// RE patternH = new RE("<.*?>");
		// int i = 0;
		// String resp1 = new String(resp);
		// try {
		// while (pattern.match(resp, i)) {
		// myLog(pattern.getParen(1));
		// resp1 = StringUtil.replaceAll(resp1, pattern.getParen(1),
		// patternH.subst(pattern.getParen(1), "").trim());
		// i = pattern.getParenEnd(0);
		// }
		// } catch (Exception e) {
		// if (Display.getInstance().getCurrent() instanceof Dialog)
		// ((Dialog) Display.getInstance().getCurrent()).dispose();
		// e.printStackTrace();
		// } catch (Error e){
		// if (Display.getInstance().getCurrent() instanceof Dialog)
		// ((Dialog) Display.getInstance().getCurrent()).dispose();
		// e.printStackTrace();
		// }

		pattern = new RE("<lj-embed.*?>");
		int i = 0;
		String resp1 = new String(resp);
		while (pattern.match(resp, i)) {
			// myLog(pattern.getParen(0));
			resp1 = StringUtil
					.replaceAll(resp1,
							pattern.getParen(
									0),
							"<br><a href=\"http://"
									+ (("top".equals(lj_journal_name) || "friends".equals(lj_journal_name))
											? (currentMyItem.getCommunity().length() > 0 ? currentMyItem.getCommunity()
													: currentMyItem.getAuthor())
											: lj_journal_name)
									+ ".livejournal.com/" + selectedID + ".html\">Полная версия с видео</a><br>");
			i = pattern.getParenEnd(0);
		}

		// resp = pattern.subst(resp, "<br><a href=\"http://" + lj_journal_name
		// + ".livejournal.com/" + selectedID +
		// ".html\">Полная версия с видео</a><br>", RE.REPLACE_ALL);

		pattern = new RE("<lj user=\"(.*?)\".*?>");
		i = 0;
		while (pattern.match(resp, i)) {
			// myLog(pattern.getParen(0));
			resp1 = StringUtil.replaceAll(resp1, pattern.getParen(0),
					"<a href=\"http://" + pattern.getParen(1) + ".livejournal.com\">" + pattern.getParen(1) + "</a>");
			i = pattern.getParenEnd(0);
		}
		// resp = pattern.subst(resp,
		// "<a href=\"http://$1.livejournal.com\">$1</a>",
		// RE.REPLACE_BACKREFERENCES);
		resp = resp1;
		return resp;
	}

	private String adaptHTML(String resp) {

		RE pattern = new RE("width=[\"'].*?[\"']", RE.MATCH_CASEINDEPENDENT);
		resp = pattern.subst(resp, " ", RE.REPLACE_ALL);
		pattern = new RE("height=[\"'].*?[\"']", RE.MATCH_CASEINDEPENDENT);
		resp = pattern.subst(resp, " ", RE.REPLACE_ALL);
		pattern = new RE("<img", RE.MATCH_CASEINDEPENDENT);
		resp = pattern.subst(resp, "<img style=\"max-width: 100% !important; height:auto !important\"", RE.REPLACE_ALL);

		return resp;
	}

	public class MyConnectionRequest extends ConnectionRequest {
		private String aTag;
		private String aURL;
		private int skipNum = 0;

		public String getTag() {
			return aTag;
		}

		public String getMyURL() {
			return aURL;
		}

		public void setMyURL(String aURL) {
			this.aURL = aURL;
		}

		public void setTag(String Tag) {
			this.aTag = Tag;
		}

		public int getSkipNum() {
			return skipNum;
		}

		public void setSkipNum(int skipNum) {
			this.skipNum = skipNum;
		}

		@Override
		protected void cookieReceived(Cookie c) {
			myLog("cookieReceived:" + c.getName() + ":" + c.getValue());
			super.cookieReceived(c);
		}

		@Override
		protected void cookieSent(Cookie c) {
			if ("langpref".equals(c.getName()) && c.getValue() != null) {
				myLog("cookieSent:" + c.getName() + ":" + c.getValue());
				if (c.getValue().indexOf("/") > 0) {
					c.setValue("ru" + c.getValue().substring(c.getValue().indexOf("/")));
				} else {
					c.setValue("ru");
				}

			}

			if (selectedURL != null && selectedURL.startsWith("http://m.livejournal.com/ratings/posts/visitors")) {
				if ("langpref".equals(c.getName()) || "rating_show_custom".equals(c.getName())) {
					myLog("cookieSent:" + c.getName() + ":" + c.getValue());
					super.cookieSent(c);
				}
			} else {
				myLog("cookieSent:" + c.getName() + ":" + c.getValue());
				super.cookieSent(c);
			}
		}

		@Override
		protected String initCookieHeader(String cookie) {
			myLog("initCookieHeader:" + selectedURL);
			myLog("visitors cookie:" + cookie);
			// return cookie;
			if (selectedURL != null && selectedURL.startsWith("http://m.livejournal.com/ratings/posts/visitors")) {
				if (cookie != null && cookie.indexOf("langpref") >= 0) {
					cookie = cookie.substring(cookie.indexOf("langpref"));
					if (cookie.indexOf(";") > 0) {
						cookie = cookie.substring(0, cookie.indexOf(";"));
					}
					if (cookie.indexOf("/") > 0) {
						cookie = "langpref=ru" + cookie.substring(cookie.indexOf("/"));
					} else {
						cookie = "langpref=ru";
					}
				} else {
					cookie = "langpref=ru";
				}
				myLog("visitors cookie2:" + "rating_show_custom=1;" + cookie);
				super.initCookieHeader("rating_show_custom=1;" + cookie);
				return "rating_show_custom=1;" + cookie;
			} else {
				if (cookie != null && cookie.indexOf("langpref") >= 0) {
					String cookie2 = cookie.substring(cookie.indexOf("langpref"));
					myLog(cookie2);
					String cookie3 = "";
					if (cookie2.indexOf(";") > 0) {
						cookie3 = cookie2.substring(cookie2.indexOf(";"));
						cookie2 = cookie2.substring(0, cookie2.indexOf(";"));
					}
					if (cookie2.indexOf("/") > 0) {
						cookie2 = "langpref=ru" + cookie2.substring(cookie2.indexOf("/"));
					} else {
						cookie2 = "langpref=ru";
					}
					myLog(cookie2);
					cookie = cookie.substring(0, cookie.indexOf("langpref")) + cookie2 + cookie3;
					myLog(cookie);
				} else {
					if (cookie != null && cookie.length() > 0) {
						cookie = cookie + ";langpref=ru";
					} else {
						cookie = "langpref=ru";
					}
				}

				myLog("visitors cookie2:" + cookie);
				super.initCookieHeader(cookie);
				return cookie;
			}
			// if (selectedURL != null &&
			// selectedURL.startsWith("http://m.livejournal.com/ratings/posts/visitors"))
			// {
			// myLog("visitors cookie:" + cookie);
			// super.initCookieHeader("rating_show_custom=1;langpref=ru/1469450983;");
			// return "rating_show_custom=1;langpref=ru/1469450983;";
			// } else {
			// myLog("-----------" + (cookie != null && cookie.length() > 0
			// ? cookie + ";rating_show_custom=1;langpref=ru/1469450983;" :
			// cookie));
			// super.initCookieHeader(cookie != null && cookie.length() > 0
			// ? cookie + ";rating_show_custom=1;langpref=ru/1469450983;" :
			// cookie);
			// return (cookie != null && cookie.length() > 0 ? cookie +
			// ";rating_show_custom=1;langpref=ru/1469450983;"
			// : cookie);
			// }
		}

		@Override
		protected void handleErrorResponseCode(int code, String message) {
			switch (code) {
			case 404:
				if (!isSaving) {
					if (Display.getInstance().getCurrent() instanceof Dialog)
						((Dialog) Display.getInstance().getCurrent()).dispose();
					Dialog.show("Ошибка " + code, "Страница не найдена.", "   OK   ", null);
				}
				break;
			case 503:
				if (!isSaving) {
					if (Display.getInstance().getCurrent() instanceof Dialog)
						((Dialog) Display.getInstance().getCurrent()).dispose();
					Dialog.show("Ошибка " + code, "Сервер недоступен.", "   OK   ", null);
				}
				break;
			default:
				if (!isSaving) {
					if (Display.getInstance().getCurrent() instanceof Dialog)
						((Dialog) Display.getInstance().getCurrent()).dispose();
					Dialog.show("Ошибка " + code, "Не удалось получить данные с сервера.", "   OK   ", null);
					// while
					// (NetworkManager.getInstance().enumurateQueue().hasMoreElements()){
					// NetworkManager.getInstance().killAndWait((ConnectionRequest)NetworkManager.getInstance().enumurateQueue().nextElement());
					// }
				}
				break;
			}

		}
	}

	public class MyItem implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String URL = "";
		private String name = "";
		private String date = "";
		private String id = "";
		private String author = "";
		private String community = "";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getURL() {
			return URL;
		}

		public void setURL(String uRL) {
			URL = uRL;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getCommunity() {
			return community;
		}

		public void setCommunity(String community) {
			this.community = community;
		}

	}

	// private void getAuthCookie(String challenge) {
	// myLog(user_login.get(0) + " " + user_login.get(1));
	//
	// MyConnectionRequest requestElement = new MyConnectionRequest() {
	//
	// // InfiniteProgress ip = new InfiniteProgress();
	// // Dialog dlg = ip.showInifiniteBlocking();
	// String[] responseArr;
	//
	// @Override
	// protected void postResponse() {
	// myLog("Register postResponse");
	//
	// for (int i = 0; i < responseArr.length; i++) {
	// if ("ljsession".equals(responseArr[i])) {
	// if ((i + 1) < responseArr.length) {
	// authCookie = responseArr[i + 1];
	// // if (user_login != null && user_login.size() > 0)
	// isRegistered = true;
	// }
	// break;
	// } else if ("errmsg".equals(responseArr[i])) {
	// Dialog.show("Ошибка", responseArr[i + 1], "OK", null);
	// authCookie = null;
	// isRegistered = false;
	// break;
	// }
	// }
	//
	// // Cookie.setAutoStored(true);
	// // Date date=new Date();
	// // Cookie ljsession = new Cookie();
	// // ljsession.setDomain("livejournal.com");
	// // ljsession.setName("ljsession");
	// // ljsession.setValue(authCookie);
	// // ljsession.setPath("/");
	// // ljsession.setExpires(date.getTime()+24*60*60*1000);
	//
	// }
	//
	// protected void readResponse(InputStream input) throws IOException {
	//
	// // resp="";
	// String response = Util.readToString(input, "UTF-8");
	// responseArr = Util.split(response, "\n");
	// // resp = Util.decode(responseArr[3], "UTF-8", false);
	// myLog(response);
	//
	// }
	// };
	//
	// MD5 md5pas;
	// if (user_login != null && user_login.size() > 1)
	// md5pas = new MD5(user_login.get(1));
	// else
	// md5pas = new MD5(lj_password);
	//
	// MD5 md5 = new MD5(challenge + md5pas.asHex());
	//
	// requestElement.setUrl(lj_flat_url);
	//
	// requestElement.removeAllArguments();
	// requestElement.setPost(true);
	//
	// requestElement.addArgument("mode", "sessiongenerate");
	// if (user_login != null && user_login.size() > 1)
	// requestElement.addArgument("user", user_login.get(0));
	// else
	// requestElement.addArgument("user", lj_login);
	// requestElement.addArgument("auth_method", "challenge");
	// requestElement.addArgument("auth_challenge", challenge);
	// requestElement.addArgument("auth_response", md5.asHex());
	//
	// // requestElement.addArgument("ver", "1");
	// // requestElement.addArgument("ditemid", selectedID);
	//
	// NetworkManager.getInstance().addToQueueAndWait(requestElement);
	//
	// }
	// protected void deAuthorizeUser() {
	//
	// if (isAuthorizing)
	// return;
	//
	// isAuthorizing = true;
	//
	// //Cookie.clearCookiesFromStorage();
	//
	// ConnectionRequest requestElement = new ConnectionRequest() {
	//
	//
	// @Override
	// protected void postResponse() {
	// }
	//
	// protected void readResponse(InputStream input) throws IOException {
	//
	// // resp="";
	// String response = Util.readToString(input, "UTF-8");
	// // responseArr = Util.split(response, "\n");
	// // // resp = Util.decode(responseArr[3], "UTF-8", false);
	// myLog(response);
	//
	// }
	//
	// @Override
	// protected void readHeaders(Object connection) throws IOException {
	//
	// String[] he = getHeaderFieldNames(connection);
	// for (String h : he) {
	// myLog(h + ": " + getHeader(connection, h));
	// }
	// }
	//
	// @Override
	// public boolean onRedirect(String url) {
	// myLog(url);
	// return true;
	// }
	//
	// };
	//
	// //http://www.livejournal.com/logout.bml?nojs=1&user=tw2fan&sessid=669&ret=http://m.livejournal.com/ratings/posts/visitors&ret_fail=http://m.livejournal.com/ratings/posts/visitors
	// requestElement.setUrl("http://www.livejournal.com/logout.bml?nojs=1");
	//
	// requestElement.removeAllArguments();
	// requestElement.setPost(true);
	//
	// // requestElement.addArgument("mode", "getchallenge");
	// if (user_login != null && user_login.size() > 1) {
	// requestElement.addArgument("user", user_login.get(0));
	// //requestElement.addArgument("password", user_login.get(1));//
	// lj_password);
	//
	// requestElement.addArgument("x", "34");
	// requestElement.addArgument("y", "19");
	// requestElement.addArgument("returnto",
	// "http://m.livejournal.com/ratings/posts/visitors");
	// requestElement.addArgument("ret",
	// "http://m.livejournal.com/ratings/posts/visitors");
	// requestElement.addArgument("ret_fail",
	// "http://m.livejournal.com/ratings/posts/visitors");
	// //requestElement.addArgument("back_uri",
	// "http://m.livejournal.com/ratings/posts/visitors");
	// // requestElement.addArgument("selecttype", "one");
	// // requestElement.addArgument("ver", "1");
	// // requestElement.addArgument("ditemid", selectedID);
	// isNetworkError = false;
	// NetworkManager.getInstance().addToQueueAndWait(requestElement);
	// }
	//// else {
	//// isAuthorizing = false;
	//// login();
	//// }
	// isAuthorizing = false;
	// }

	protected void authorizeUser() {

		if (isAuthorizing)
			return;

		isAuthorizing = true;

		Cookie.clearCookiesFromStorage();

		ConnectionRequest requestElement = new ConnectionRequest() {

			// InfiniteProgress ip = new InfiniteProgress();
			// Dialog dlg = ip.showInifiniteBlocking();
			// String[] responseArr;
			// String challenge;

			@Override
			protected void postResponse() {
				// myLog("Register postResponse");
				//
				// for (int i = 0; i < responseArr.length; i++) {
				// if (responseArr[i].equals("challenge")) {
				// if ((i + 1) < responseArr.length) {
				// challenge = responseArr[i + 1];
				// }
				// }
				// }
				//
				// if (challenge != null) {
				// getAuthCookie(challenge);
				//
				// }
				// myLog(resp);
				// MD5 md5 = new MD5(resp);
				// String md5Password = md5.asHex();
			}

			protected void readResponse(InputStream input) throws IOException {

				// resp="";
				String response = Util.readToString(input, "UTF-8");
				// responseArr = Util.split(response, "\n");
				// // resp = Util.decode(responseArr[3], "UTF-8", false);
				myLog(response);

			}

			@Override
			protected void readHeaders(Object connection) throws IOException {

				String[] he = getHeaderFieldNames(connection);
				for (String h : he) {
					myLog(h + ": " + getHeader(connection, h));
				}
				// myLog("set-cookie: "+getHeader(connection,
				// "set-cookie"));// or set-cookie depending on how it is
				// written in the header.
				// myLog("ljsession: "+getHeader(connection,
				// "ljsession"));
				// myLog(sessionID);
			}

			@Override
			public boolean onRedirect(String url) {
				myLog(url);
				if (url.indexOf("login?error") > 0) {
					isRegistered = false;
				} else if (url.indexOf("read/friends") > 0) {
					isRegistered = true;
				}
				return true;
			}

		};

		requestElement.setUrl("http://www.livejournal.com/login.bml?nojs=1");

		requestElement.removeAllArguments();
		requestElement.setPost(true);

		// requestElement.addArgument("mode", "getchallenge");
		if (user_login != null && user_login.size() > 1) {
			requestElement.addArgument("user", user_login.get(0));
			requestElement.addArgument("password", user_login.get(1));// lj_password);

			requestElement.addArgument("x", "34");
			requestElement.addArgument("y", "19");
			requestElement.addArgument("returnto", "http://m.livejournal.com/read/friends");
			requestElement.addArgument("ret_fail", "http://m.livejournal.com/login?error=");
			requestElement.addArgument("back_uri", "/read/friends");
			// requestElement.addArgument("selecttype", "one");
			// requestElement.addArgument("ver", "1");
			// requestElement.addArgument("ditemid", selectedID);
			isNetworkError = false;
			NetworkManager.getInstance().addToQueueAndWait(requestElement);
		} else {
			isAuthorizing = false;
			login();
		}
		isAuthorizing = false;
	}

	protected void setChallengeResponse() {
		authChallenge = null;
		authResponse = null;
		MyConnectionRequest requestElement = new MyConnectionRequest() {

			// InfiniteProgress ip = new InfiniteProgress();
			// Dialog dlg = ip.showInifiniteBlocking();
			String[] responseArr;
			String challenge;

			@Override
			protected void postResponse() {
				myLog("Register postResponse");

				for (int i = 0; i < responseArr.length; i++) {
					if (responseArr[i].equals("challenge")) {
						if ((i + 1) < responseArr.length) {
							challenge = responseArr[i + 1];
						}
					}
				}

				if (challenge != null) {
					authChallenge = challenge;
					MD5u md5pas;
					if (user_login != null && user_login.size() > 1)
						md5pas = new MD5u(user_login.get(1));
					else
						md5pas = new MD5u(lj_password);
					MD5u mD5u = new MD5u(challenge + md5pas.asHex());
					authResponse = mD5u.asHex();
				}
				myLog(authChallenge);
				myLog(authResponse);
				// MD5 md5 = new MD5(resp);
				// String md5Password = md5.asHex();
			}

			protected void readResponse(InputStream input) throws IOException {

				// resp="";
				String response = Util.readToString(input, "UTF-8");
				responseArr = Util.split(response, "\n");
				// resp = Util.decode(responseArr[3], "UTF-8", false);
				myLog(response);

			}
		};

		requestElement.setUrl(lj_flat_url);

		requestElement.removeAllArguments();
		requestElement.setPost(true);

		requestElement.addArgument("mode", "getchallenge");
		// requestElement.addArgument("user", lj_login);
		// requestElement.addArgument("password", lj_password);
		// requestElement.addArgument("usejournal", lj_journal_name);
		// requestElement.addArgument("selecttype", "one");
		// requestElement.addArgument("ver", "1");
		// requestElement.addArgument("ditemid", selectedID);

		NetworkManager.getInstance().addToQueueAndWait(requestElement);

	}

	protected void postMain(MForm f) {
		// myLog(f.getCustomTitle());

	}

	protected void beforeMain(final MForm f) {
		// f.getCustomTitle().putClientProperty("CTitle", Boolean.TRUE);
		// myLog(f.getCustomTitle().getClientProperty("CTitle"));
		// Resources res = fetchResourceFile();
		// String[] themes = res.getThemeResourceNames();
		// myLog(themes[0]+" "+themes[1]);
		// int THEME_INDEX = 1;
		// UIManager.getInstance().setThemeProps(res.getTheme(themes[THEME_INDEX]));
		// //Display.getInstance().getCurrent().refreshTheme();

		Display.getInstance().addEdtErrorHandler(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (Display.getInstance().getCurrent() instanceof Dialog)
					((Dialog) Display.getInstance().getCurrent()).dispose();
				isErrorShowed = true;
				Dialog.show("Ошибка", "Случилось непредвиденное :(", "OK", null);
				evt.consume();
			}
		});

		NetworkManager.getInstance().addErrorListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {

				if ((!isSaving || (isSaving && !isErrorShowed)) && !isAuthorizing) {
					if (Display.getInstance().getCurrent() instanceof Dialog)
						((Dialog) Display.getInstance().getCurrent()).dispose();
					Dialog.show("Ошибка", "Не удалось получить данные с сервера.", "   OK   ", null);

					isErrorShowed = true;
					// while
					// (NetworkManager.getInstance().enumurateQueue().hasMoreElements()){
					// NetworkManager.getInstance().killAndWait((ConnectionRequest)NetworkManager.getInstance().enumurateQueue().nextElement());
					// }

				}
				isNetworkError = true;
				evt.consume();
			}
		});

		if (isUsers)
			showUsers(f);
		else
			showCommunities(f);

		// authorizeUser();
	}

	// private void placeCommand(Command c, String place) {
	// if (place != null) {
	// if (place.equalsIgnoreCase("right"))
	// c.putClientProperty(SideMenuBar.COMMAND_PLACEMENT_KEY,
	// SideMenuBar.COMMAND_PLACEMENT_VALUE_RIGHT);
	// }
	// }

	private void addMainCommands(final MForm f) {

		f.removeAllCommands();
		if (!isIOS) {
			Command c = new Command("ЖУРНАЛЫ");
			Label l = new Label("ЖУРНАЛЫ") {

				public void paint(Graphics g) {
					super.paint(g);
					g.drawLine(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight() - 1);
				}
			};
			l.setUIID("Separator");
			c.putClientProperty("SideComponent", l);
			// c.putClientProperty("place", "side");
			f.addCommand(c);
		}

		Command users = new Command("Блогеры", theme.getImage("6_social_person_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				// resetTransitions();
				isTop = false;
				isFriends = false;
				isUsers = true;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("123", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				// myLog(((SideMenuBar)f.getMenuBar()).getCommandCount());
				if (((SideMenuBar) f.getMenuBar()).getCommandCount() <= 0)
					addMainCommands(f);
				showUsers(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};

		// users.putClientProperty("place", "side");
		f.addCommand(users);

		Command communities = new Command("Сообщества", theme.getImage("6_social_group_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				// resetTransitions();
				isTop = false;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("123", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showCommunities(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};

		// communities.putClientProperty("place", "side");
		f.addCommand(communities);

		Command top25 = new Command("Топ", theme.getImage("1_top_users_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				// resetTransitions();

				isTop = true;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showTop(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};

		// communities.putClientProperty("place", "side");
		f.addCommand(top25);

		Command friends = new Command("Друзья", theme.getImage("1_friends_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				// resetTransitions();

				isTop = false;
				isFriends = true;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Друзья", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showFriends(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};

		// communities.putClientProperty("place", "side");
		f.addCommand(friends);

		Command saved = new Command("Сохраненные", theme.getImage("4_collections_collection_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				// resetTransitions();
				isTop = false;
				isFriends = false;
				isUsers = false;
				isSaved = true;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("123", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showSavedJournals(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};

		// saved.putClientProperty("place", "side");
		f.addCommand(saved);

		if (!isIOS) {
			Command c = new Command("НАСТРОЙКИ");
			Label l = new Label("НАСТРОЙКИ") {

				public void paint(Graphics g) {
					super.paint(g);
					g.drawLine(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight() - 1);
				}
			};
			l.setUIID("Separator");
			c.putClientProperty("SideComponent", l);
			c.putClientProperty("place", "side");
			f.addCommand(c);
		}
		if (!isIOS) {

			if (isNight) {
				Command daynight = new Command("Ночь", theme.getImage("10_device_access_bightness_low_l.png")) {
					public void actionPerformed(ActionEvent ev) {
						// resetTransitions();
						isNight = false;
						UIManager.getInstance().setThemeProps(theme.getTheme(lightTheme));
						resetTransitions();
						// Display.getInstance().getCurrent().refreshTheme();
						if (communitiesForm != null)
							communitiesForm.refreshTheme();
						if (tagsForm != null)
							tagsForm.refreshTheme();
						if (itemsForm != null)
							itemsForm.refreshTheme();
						if (savedItemsForm != null)
							savedItemsForm.refreshTheme();

						((SideMenuBar) Display.getInstance().getCurrent().getMenuBar()).refreshTheme();
						// ((CustomSideMenuBar)
						// f.getMenuBar()).removeAllSideCommands();
						if (browserForm != null) {
							browserForm.refreshTheme();
							WebBrowser myWb = (WebBrowser) browserForm.getClientProperty("myWb");

							if (myWb != null) {
								myWb.setPage(getLightHeader() + (isPinchToZoom ? wbPageOriginal : wbPage) + htmlFooter,
										null);
							}
						}
						Storage.getInstance().writeObject("isNight", isNight);

						// Command back = new Command("Назад") {
						// public void actionPerformed(ActionEvent ev) {
						// if (isUsers)
						// showUsers(communitiesForm);
						// else
						// showCommunities(communitiesForm);
						// }
						// };
						// // myLog("showItemsForm " +
						// selectedTag);
						// f.setBackCommand(back);

						// addMainCommands(f);
						f.removeAllCommands();
						addMainCommands(f);

					}
				};
				// placeCommand(settings,"right");
				// daynight.putClientProperty("place", "side");
				f.addCommand(daynight);
			} else {
				Command daynight = new Command("День", theme.getImage("10_device_access_brightness_high_l.png")) {
					public void actionPerformed(ActionEvent ev) {
						// resetTransitions();
						isNight = true;
						UIManager.getInstance().setThemeProps(theme.getTheme(darkTheme));
						resetTransitions();
						if (communitiesForm != null)
							communitiesForm.refreshTheme();
						if (tagsForm != null)
							tagsForm.refreshTheme();
						if (itemsForm != null)
							itemsForm.refreshTheme();
						if (savedItemsForm != null)
							savedItemsForm.refreshTheme();

						((SideMenuBar) Display.getInstance().getCurrent().getMenuBar()).refreshTheme();
						// ((CustomSideMenuBar)
						// f.getMenuBar()).removeAllSideCommands();
						if (browserForm != null) {
							browserForm.refreshTheme();
							WebBrowser myWb = (WebBrowser) browserForm.getClientProperty("myWb");

							if (myWb != null) {
								myWb.setPage(getDarkHeader() + (isPinchToZoom ? wbPageOriginal : wbPage) + htmlFooter,
										null);
							}
						}
						Storage.getInstance().writeObject("isNight", isNight);

						// Command back = new Command("Назад") {
						// public void actionPerformed(ActionEvent ev) {
						// if (isUsers)
						// showUsers(f);
						// else
						// showCommunities(f);
						// }
						// };
						// // myLog("showItemsForm " +
						// selectedTag);
						// f.setBackCommand(back);
						f.removeAllCommands();
						addMainCommands(f);
					}
				};
				// placeCommand(settings,"right");
				// daynight.putClientProperty("place", "side");
				f.addCommand(daynight);
			}
		}
		Command hide = new Command("Видимость", theme.getImage("3_rating_half_important_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				// resetTransitions();
				isShowHide = true;
				communitiesForm.show();
				if (isUsers) {
					showUsers(communitiesForm);
				} else {
					showCommunities(communitiesForm);
				}
			}
		};
		// placeCommand(hide,"right");
		// hide.putClientProperty("place", "side");
		f.addCommand(hide);

		Command fontSize = new Command("Шрифт", theme.getImage("2_action_settings_l.png")) {
			public void actionPerformed(ActionEvent ev) {

				showFontSizeDlg();
				if (browserForm != null) {
					// communitiesbrowserForm.refreshTheme();
					WebBrowser myWb = (WebBrowser) browserForm.getClientProperty("myWb");

					if (myWb != null) {
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader())
								+ (isPinchToZoom ? wbPageOriginal : wbPage) + htmlFooter, null);
					}
				}
				// resetTransitions();
				// Hashtable h = new Hashtable();
				// h.put("font", Font.createSystemFont(Font.FACE_SYSTEM,
				// Font.STYLE_PLAIN, Font.SIZE_LARGE));
				// UIManager.getInstance().addThemeProps(h);
				// Display.getInstance().getCurrent().refreshTheme();
				//
				// isShowHide = true;
				// communitiesForm.show();
				// if (isUsers) {
				// showUsers(communitiesForm);
				// } else {
				// showCommunities(communitiesForm);
				// }
			}
		};
		// placeCommand(hide,"right");
		// hide.putClientProperty("place", "side");
		f.addCommand(fontSize);

		if (!isIOS) {
			Command c = new Command("");
			Label l = new Label(" ") {

				public void paint(Graphics g) {
					super.paint(g);
					g.drawLine(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight() - 1);
				}
			};
			l.setUIID("Separator");
			c.putClientProperty("SideComponent", l);
			c.putClientProperty("place", "side");
			f.addCommand(c);
		}

		Command exit = new Command("Выйти", theme.getImage("1_navigation_exit_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				Display.getInstance().exitApplication();
			}
		};
		// exit.putClientProperty("place", "side");
		f.addCommand(exit);

		((MForm) f).getCustomTitleArea().revalidate();

	}

	private void showSavedItemsForm(final MForm parent) {
		if (savedItemsForm == null)
			savedItemsForm = newMyForm(lj_journal_name, parent);
		final Container c = addLayoutY(savedItemsForm);

		Command back = new Command(parent.getTitle()) {
			public void actionPerformed(ActionEvent ev) {
				parent.showBack();
				showSavedJournals(parent);
			}
		};
		// myLog("showItemsForm " + selectedTag);
		savedItemsForm.setBackCommand(back);

		savedItemsForm.removeAllCustomCommands();
		savedItemsForm.revalidate();
		savedItemsForm.show();

		// ((CustomSideMenuBar) f.getMenuBar()).removeAllSideCommands();
		// addMainCommands(savedItemsForm);

		if (savedItems != null && savedItems.get(lj_journal_name) != null
				&& savedItems.get(lj_journal_name).size() > 0) {
			Command del = new Command("Удалить", theme.getImage("5_content_discard.png")) {
				public void actionPerformed(ActionEvent ev) {
					// if (!isBusy) {
					if (!isShowRemove) {
						isShowRemove = true;
						showSavedItems(c);
					} else {
						isShowRemove = false;
						showSavedItems(c);
					}

					// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
					// }
				}
			};

			// users.putClientProperty("TitleCommand", Boolean.TRUE);
			savedItemsForm.addCustomCommand(del);
		}

		if (((SideMenuBar) savedItemsForm.getMenuBar()).getCommandCount() <= 0)
			addMainCommands(savedItemsForm);

		showSavedItems(c);

	}

	private void showSavedItems(final Container c) {

		if (c.getComponentForm().getClientProperty("OKButton") != null) {
			c.getComponentForm().removeComponent((Component) c.getComponentForm().getClientProperty("OKButton"));
		}

		c.removeAll();

		((MForm) c.getComponentForm()).setCustomTitle(lj_journal_name + " - " + "cохраненные");

		HashMap<String, HashMap<String, String>> l = savedItems.get(lj_journal_name);

		final String sname = new String(lj_journal_name);

		if (l != null) {
			for (final HashMap<String, String> entry : l.values()) {
				// myLog(uname + " " +
				// lj_communities_list.get(uname).get("name"));
				if (!isShowRemove) {
					final MultiButton mb = new MultiButton();

					mb.setCommand(new Command("") {
						public void actionPerformed(ActionEvent ev) {

							lj_journal_name = sname;
							clickedItem = null;
							clickedTagItem = null;
							if (currentMyItem == null)
								currentMyItem = new MyItem();
							currentMyItem.setAuthor(entry.get("author"));
							currentMyItem.setCommunity(entry.get("community") != null ? entry.get("community") : "");
							currentMyItem.setDate(entry.get("date"));
							currentMyItem.setId(entry.get("id"));
							currentMyItem.setName(entry.get("name"));

							savedItemId = ("top".equals(lj_journal_name) || "friends".equals(lj_journal_name))
									? (entry.get("community") != null && entry.get("community").length() > 0
											? entry.get("community") : entry.get("author")) + "." + entry.get("id")
									: entry.get("id");
							showBrowserForm(savedItemsForm);
							// showSavedItemsForm(f);
						}
					});

					mb.setEmblem(null);
					mb.setHorizontalLayout(false);
					mb.setTextLine1(entry.get("name"));
					if (entry.get("author") != "")
						mb.setTextLine2(entry.get("author")
								+ (entry.get("community") != null && entry.get("community").length() > 0
										&& !entry.get("community").equals(m_lj_journal_name)
												? "(" + entry.get("community") + ")" : "")
								+ (entry.get("author").length() > 0 && entry.get("date").length() > 0 ? " - " : "")
								+ entry.get("date"));
					else
						mb.setTextLine2(entry.get("date"));

					c.addComponent(mb);

				} else {
					MultiButton mb = new MultiButton();
					mb.putClientProperty("itemID",
							(("top".equals(lj_journal_name) || "friends".equals(lj_journal_name))
									? (entry.get("community") != null && entry.get("community").length() > 0
											? entry.get("community") : entry.get("author")) + "." + entry.get("id")
									: entry.get("id")));
					mb.setEmblem(null);
					mb.setHorizontalLayout(false);
					mb.setTextLine1(entry.get("name"));
					if (entry.get("author") != "")
						mb.setTextLine2(entry.get("author")
								+ (entry.get("community") != null && entry.get("community").length() > 0
										? "(" + entry.get("community") + ")" : "")
								+ (entry.get("author").length() > 0 && entry.get("date").length() > 0 ? " - " : "")
								+ entry.get("date"));
					else
						mb.setTextLine2(entry.get("date"));

					mb.setCheckBox(true);
					// CheckBox ch=new CheckBox();
					//
					// mb.addComponent(BorderLayout.WEST, new CheckBox());
					// mb.setLeadComponent(ch);
					// mb.putClientProperty("Checkbox", ch);

					mb.revalidate();
					c.addComponent(mb);
				}

			}

			if (isShowRemove) {
				Button ok = new Button(new Command("ОК") {
					public void actionPerformed(ActionEvent ev) {
						isShowRemove = false;
						removeSavedItems(c);
					}
				});

				ok.setUIID("DialogButtonCommand");
				Container fc = new Container(new FlowLayout(Component.CENTER));
				fc.addComponent(ok);
				ok.setPreferredW((int) Math.round((double) c.getComponentForm().getPreferredW() / 1.2));
				fc.setUIID("DialogCommandArea");

				c.getComponentForm().addComponent(BorderLayout.SOUTH, fc);
				c.getComponentForm().putClientProperty("OKButton", fc);
			}

		}
		c.getComponentForm().revalidate();

	}

	private void removeSavedItems(Container c) {

		String itemID;
		MultiButton mb;
		HashMap<String, HashMap<String, String>> l = savedItems.get(lj_journal_name);
		int num = c.getComponentCount();
		for (int i = 0; i < num; i++) {
			mb = (MultiButton) c.getComponentAt(i);
			if (mb.isSelected()) {
				itemID = (String) mb.getClientProperty("itemID");

				if (itemID != null)
					Storage.getInstance().deleteStorageFile(lj_journal_name + "." + itemID);
				l.remove(itemID);

			}
		}
		// showStorage();
		savedItems.remove(lj_journal_name);

		if (l.size() > 0) {
			savedItems.put(lj_journal_name, l);
			Storage.getInstance().writeObject("savedItems", savedItems);
			showSavedItems(c);
		} else {
			Storage.getInstance().writeObject("savedItems", savedItems);
			c.getComponentForm().getBackCommand().actionPerformed(null);
		}

	}

	private void removeSavedJournals(Container c) {
		MultiButton mb;

		int num = c.getComponentCount();
		for (int i = 0; i < num; i++) {
			mb = (MultiButton) c.getComponentAt(i);
			if (mb.isSelected()) {
				// myLog(mb.getTextLine2());
				String journalName = StringUtil.replaceAll(mb.getTextLine2(), "-", "_");

				HashMap<String, HashMap<String, String>> l = savedItems.get(journalName);
				if (l != null && l.size() > 0) {
					for (String itemID : l.keySet()) {
						if (itemID != null)
							Storage.getInstance().deleteStorageFile(journalName + "." + itemID);
					}

				}
				savedItems.remove(journalName);

				journalName = StringUtil.replaceAll(mb.getTextLine2(), "_", "-");

				l = savedItems.get(journalName);
				if (l != null && l.size() > 0) {
					for (String itemID : l.keySet()) {
						if (itemID != null)
							Storage.getInstance().deleteStorageFile(journalName + "." + itemID);
					}

				}
				savedItems.remove(journalName);

			}
		}

		Storage.getInstance().writeObject("savedItems", savedItems);
		// showStorage();
	}

	private void showSavedJournals(final MForm f) {
		// f.removeAll();
		if (f.getClientProperty("OKButton") != null) {
			f.removeComponent((Component) f.getClientProperty("OKButton"));
		}

		final Container mainContainer = addLayoutY(f);

		f.setCustomTitle("Сохраненные");

		// f.removeAllCommands();
		// ((CustomSideMenuBar) f.getMenuBar()).removeAllSideCommands();
		f.removeAllCustomCommands();

		Command exit = new Command("Выйти") {
			public void actionPerformed(ActionEvent ev) {

				if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
					Display.getInstance().exitApplication();
				}

			}
		};

		f.setBackCommand(exit);

		// addMainCommands(f);

		if (savedItems.size() > 0) {
			Command del = new Command("Удалить", theme.getImage("5_content_discard.png")) {
				public void actionPerformed(ActionEvent ev) {
					// if (!isBusy) {
					if (!isShowRemove) {
						isShowRemove = true;
						showSavedJournals(f);
					} else {
						isShowRemove = false;
						showSavedJournals(f);
					}
					// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
					// }
				}
			};

			// del.putClientProperty("TitleCommand", Boolean.TRUE);
			f.addCustomCommand(del);
		}

		for (final String uname : savedItems.keySet()) {
			String uname1 = StringUtil.replaceAll(uname, "_", "-");
			// myLog(uname + " " +
			// lj_communities_list.get(uname).get("name"));
			if (!isShowRemove) {
				final MultiButton mb = new MultiButton();

				mb.setCommand(new Command("") {
					public void actionPerformed(ActionEvent ev) {

						lj_journal_name = uname;
						m_lj_journal_name = StringUtil.replaceAll(uname, "-", "_");
						clickedItem = null;
						clickedTagItem = null;
						showSavedItemsForm(f);
					}
				});

				Label num = new Label();
				num.setText("" + savedItems.get(uname).size());
				num.setUIID("LeftMbLabel");
				mb.addComponent(BorderLayout.WEST, num);
				mb.setEmblem(null);
				mb.setHorizontalLayout(false);
				if (lj_communities_list.containsKey(uname1))
					mb.setTextLine1(lj_communities_list.get(uname1).get("name"));
				else if (lj_users_list.containsKey(uname1))
					mb.setTextLine1(lj_users_list.get(uname1).get("name"));
				else
					mb.setTextLine1(uname1);
				mb.setTextLine2(uname1);

				mainContainer.addComponent(mb);

			} else {
				final MultiButton mb = new MultiButton();
				Label num = new Label();
				num.setText("" + savedItems.get(uname).size());
				num.setUIID("LeftMbLabel");
				mb.addComponent(BorderLayout.WEST, num);
				mb.setEmblem(null);
				mb.setHorizontalLayout(false);
				if (lj_communities_list.containsKey(uname1))
					mb.setTextLine1(lj_communities_list.get(uname1).get("name"));
				else if (lj_users_list.containsKey(uname1))
					mb.setTextLine1(lj_users_list.get(uname1).get("name"));
				else
					mb.setTextLine1(uname1);
				mb.setTextLine2(uname1);
				// CheckBox ch=new CheckBox();
				//
				// mb.addComponent(BorderLayout.WEST, new CheckBox());
				// mb.setLeadComponent(ch);
				// mb.putClientProperty("Checkbox", ch);
				mb.setCheckBox(true);

				mb.revalidate();
				mainContainer.addComponent(mb);
			}

		}

		if (isShowRemove) {
			Button ok = new Button(new Command("ОК") {
				public void actionPerformed(ActionEvent ev) {

					isShowRemove = false;
					removeSavedJournals(mainContainer);
					showSavedJournals(f);
				}
			});

			ok.setUIID("DialogButtonCommand");
			Container fc = new Container(new FlowLayout(Component.CENTER));
			ok.setPreferredW((int) Math.round((double) f.getPreferredW() / 1.2));
			fc.addComponent(ok);
			fc.setUIID("DialogCommandArea");

			f.addComponent(BorderLayout.SOUTH, fc);
			f.putClientProperty("OKButton", fc);
		}

		f.revalidate();
	}

	private void showFontSizeDlg() {
		final Dialog dlg = new Dialog();
		dlg.setTitle("    Размер шрифта статей    ");

		dlg.setLayout(new BorderLayout());
		dlg.getContentPane().setUIID("DialogContentPane");
		final NumericSpinner ns = new NumericSpinner();
		ns.setMax(16);
		ns.setMin(-16);
		ns.setStep(1);
		ns.setValue(fontSize);

		// TextField.setUseNativeTextInput(true);
		// final TextField fSize = new TextField();
		// fSize.setText(""+fontSize);

		Container by = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		by.setUIID("DialogContentPane");
		by.addComponent(ns);
		// by.addComponent(fSize);
		dlg.addComponent(BorderLayout.CENTER, by);
		Container bc = new Container(new FlowLayout(Component.CENTER));
		bc.setUIID("DialogCommandArea");

		Button ok = new Button(new Command("ОК") {
			public void actionPerformed(ActionEvent ev) {
				fontSize = ns.getValue();
				Storage.getInstance().writeObject("fontSize", fontSize);
				dlg.dispose();
			}
		});

		ok.setUIID("DialogButtonCommand");

		// Button cancel = new Button(new Command("Отмена") {
		// public void actionPerformed(ActionEvent ev) {
		// dlg.dispose();
		// }
		// });
		// cancel.setUIID("DialogButtonCommand");
		bc.addComponent(ok);
		// bc.addComponent(cancel);
		// ok.setPreferredW(dlg.getDialogComponent().getPreferredW() / 3);
		// cancel.setPreferredW(dlg.getDialogComponent().getPreferredW() / 3);
		// Container fc = new Container(new FlowLayout(Component.CENTER));
		// fc.addComponent(bc);
		ok.setPreferredW(dlg.getDialogComponent().getPreferredW());
		dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
		dlg.revalidate();
		dlg.showPacked(BorderLayout.CENTER, true);
	}

	private void addJournalDialog(String duname, String dname) {

		final Dialog dlg = new Dialog();
		if (isUsers) {
			dlg.setTitle("Добавить журнал");
		} else {
			dlg.setTitle("Добавить сообщество");
		}

		dlg.setLayout(new BorderLayout());
		dlg.getContentPane().setUIID("DialogContentPane");

		TextField.setUseNativeTextInput(true);
		final TextField name = new TextField();
		name.setHint("Инженерия");
		final TextField uname = new TextField();
		uname.setHint("engineering-ru");

		if (duname != null && duname.length() > 0) {
			uname.setText(duname);
		}

		if (dname != null && dname.length() > 0) {
			name.setText(dname);
		}

		Container by = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		by.setUIID("DialogContentPane");
		by.addComponent(name);
		by.addComponent(uname);
		dlg.addComponent(BorderLayout.CENTER, by);
		Container bc = new Container(new FlowLayout(Component.CENTER));
		bc.setUIID("DialogCommandArea");

		Button ok = new Button(new Command("ОК") {
			public void actionPerformed(ActionEvent ev) {
				isRegistered = false;
				String jname = name.getText().trim();
				String juname = StringUtil.replaceAll(uname.getText().trim(), "_", "-");
				if (jname.length() > 0 && juname.length() > 0) {

					if (isUsers) {
						if (lj_users_list.keySet().contains(juname)) {
							dlg.dispose();
							Dialog.show("Ошибка", "Такой блогер уже есть в списке.", "OK", null);
							addJournalDialog("", jname);

						} else {
							owner_users_list.put(juname, new HashMap<String, String>());
							owner_users_list.get(juname).put("name", jname);
							Storage.getInstance().writeObject("owner_users_list", owner_users_list);
							lj_users_list.putAll(owner_users_list);
							if (!listUsers.contains(juname)) {
								listUsers.add(0, juname);
								Storage.getInstance().writeObject("Users", listUsers);
							}
							showUsers(communitiesForm);
							dlg.dispose();
						}

					} else {
						if (lj_communities_list.keySet().contains(juname)) {
							dlg.dispose();
							Dialog.show("Ошибка", "Такое сообщество уже есть в списке.", "OK", null);
							addJournalDialog("", jname);

						} else {
							owner_communities_list.put(juname, new HashMap<String, String>());
							owner_communities_list.get(juname).put("name", jname);
							Storage.getInstance().writeObject("owner_communities_list", owner_communities_list);
							lj_communities_list.putAll(owner_communities_list);
							if (!listCommunities.contains(juname)) {
								listCommunities.add(0, juname);
								Storage.getInstance().writeObject("Communities", listCommunities);
							}
							showCommunities(communitiesForm);
							dlg.dispose();
						}
					}

				} else {
					dlg.dispose();
					addJournalDialog(juname, jname);
				}

			}
		});

		ok.setUIID("DialogButtonCommand");

		Button cancel = new Button(new Command("Отмена") {
			public void actionPerformed(ActionEvent ev) {
				dlg.dispose();
			}
		});
		cancel.setUIID("DialogButtonCommand");
		bc.addComponent(ok);
		bc.addComponent(cancel);
		ok.setPreferredW(dlg.getDialogComponent().getPreferredW() / 3);
		cancel.setPreferredW(dlg.getDialogComponent().getPreferredW() / 3);
		// Container fc = new Container(new FlowLayout(Component.CENTER));
		// fc.addComponent(bc);
		dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
		dlg.revalidate();
		dlg.showPacked(BorderLayout.CENTER, true);

	}

	private void showCommunities(final MForm f) {
		savedItemId = null;
		// f.removeAll();
		if (f.getClientProperty("OKButton") != null) {
			f.removeComponent((Component) f.getClientProperty("OKButton"));
		}

		if (listCommunities != null && listCommunities.size() > 0) {
			Vector<String> newCommList = new Vector<String>(lj_communities_list.keySet());
			newCommList.removeAll(listCommunities);
			listCommunities.addAll(newCommList);
		} else {
			listCommunities = new Vector<String>(lj_communities_list.keySet());
		}

		if (listHiddenCommunities != null && listHiddenCommunities.size() > 0) {
			listCommunities.removeAll(listHiddenCommunities);
			if (isShowHide) {
				listCommunities.addAll(listHiddenCommunities);
			}
		}

		listCommunities = new Vector<String>(new LinkedHashSet<String>(listCommunities));

		f.setCustomTitle("Сообщества");

		// f.removeAllCommands();
		// ((SideMenuBar) f.getMenuBar()).;
		f.removeAllCustomCommands();

		Command exit = new Command("Выйти") {
			public void actionPerformed(ActionEvent ev) {

				if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
					Display.getInstance().exitApplication();
				}

			}
		};
		// placeCommand(exit,"right");

		f.setBackCommand(exit);

		Command addComm = new Command("Добавить", theme.getImage("1_add_new.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				addJournalDialog("", "");
				// isUsers = false;
				// if (communitiesForm == null) {
				// communitiesForm = newMyForm("Топ", null);
				// //
				// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				// }
				//
				// communitiesForm.show();
				// showFriends(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(addComm);

		Command friends = new Command("Друзья", theme.getImage("1_friends.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				isTop = false;
				isFriends = true;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Лента", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showFriends(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Топ", theme.getImage("1_top_users.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				isTop = true;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showTop(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(topUsers);

		Command users = new Command("Блогеры", theme.getImage("6_social_group.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				isTop = false;
				isFriends = false;
				isUsers = true;
				isSaved = false;
				showUsers(f);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(users);

		// users.putClientProperty("TitleCommand", Boolean.TRUE);
		// users.putClientProperty("U", value)
		// users.putClientProperty("place", "side");
		// f.addCommand(users);
		// users.putClientProperty("TitleCommand", Boolean.TRUE);

		final Container mainContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS)) {
			@Override
			protected void dragExit(Component dragged) {
				if (dragged instanceof MultiButton) {
					((MultiButton) dragged).setIcon(null);
				}
			}
		};

		mainContainer.setScrollableY(true);
		mainContainer.setDropTarget(false);

		for (final String uname : listCommunities) {
			if (lj_communities_list.get(uname) == null || "top".equals(uname) || "friends".equals(uname)) {
				continue;
			}
			// myLog(uname + " " +
			// lj_communities_list.get(uname).get("name"));
			if (!isShowHide) {
				final MultiButton mb = new MultiButton() {
					@Override
					public void longPointerPress(int x, int y) {
						longPressed = true;
						Container parent = this.getParent();
						parent.removeComponent(this);
						parent.addComponent(0, this);
						if (parent.getComponentCount() > 0)
							parent.scrollRectToVisible(0, 0, 1, parent.getHeight(), parent.getComponentAt(0));
						parent.revalidate();
						saveList(mainContainer, "Communities");
						// myLog("isDraggable " +
						// this.isDraggable());
						// myLog("LongPress "+x+" "+y);
						// this.setDraggable(true);
						// f.pointerReleased(x, y);
						// f.pointerPressed(x, y);
						// this.setIcon(theme.getImage("5_content_import_export.png"));
						// this.setIconUIID(this.getEmblemUIID());
						// this.revalidate();
						// Display.getInstance().vibrate(100);
						longPressed = false;
					}

				};

				// mb.addDropListener(new ActionListener() {
				//
				// public void actionPerformed(final ActionEvent evt) {
				// Display.getInstance().callSerially(new Runnable() {
				// public void run() {
				// // myLog("Dropped");
				// ((MultiButton) evt.getDraggedComponent()).setIcon(null);
				// evt.getDraggedComponent().setDraggable(false);
				// saveList(mainContainer, "Communities");
				// }
				// });
				// }
				// });

				// mb.setTextLine3(entry.getKey() + "3");
				// mb.setTextLine4(entry.getKey() + "4");
				// mb.setMaskName("maskImage");
				// mb.setIconUIID("Avatar");
				// mb.setIcon(r.getImage(C_AVATAR[iter]));
				// final int current = iter;

				mb.setCommand(new Command("") {
					public void actionPerformed(ActionEvent ev) {
						// if (ev.isLongEvent()){
						// myLog("LongEvent "+ev.getComponent().isDraggable());
						// ev.getComponent().setDraggable(true);
						// }else{
						// tagsList.clear();
						// itemsListRecent.clear();
						// ev.getComponent().setDraggable(true);
						// findMainContainer(f).setDropTarget(false);
						if (!longPressed) {
							mb.setIcon(null);
							selectedID = "";

							lj_journal_name = uname;
							m_lj_journal_name = StringUtil.replaceAll(uname, "-", "_");
							selectedTag = "";
							clickedItem = null;
							clickedTagItem = null;
							isTags = false;
							// selectedURL = "http://" + lj_journal_name +
							// ".livejournal.com/";
							selectedURL = "http://m.livejournal.com/read/user/" + m_lj_journal_name + "/";
							showTagsForm(f);
						}
						// }
					}
				});

				mb.setEmblem(null);
				mb.setHorizontalLayout(false);
				mb.setTextLine1(lj_communities_list.get(uname).get("name"));
				mb.setTextLine2(uname);

				mainContainer.addComponent(mb);

			} else {
				final MultiButton mb = new MultiButton();
				mb.setEmblem(null);
				mb.setHorizontalLayout(false);
				mb.setTextLine1(lj_communities_list.get(uname).get("name"));
				mb.setTextLine2(uname);
				// CheckBox ch=new CheckBox();
				//
				// mb.addComponent(BorderLayout.WEST, new CheckBox());
				// mb.setLeadComponent(ch);
				// mb.putClientProperty("Checkbox", ch);
				mb.setCheckBox(true);
				if (listHiddenCommunities != null && listHiddenCommunities.contains(uname))
					mb.setSelected(false);
				else
					mb.setSelected(true);
				mb.revalidate();
				mainContainer.addComponent(mb);
			}

		}

		if (isShowHide) {
			Button ok = new Button(new Command("ОК") {
				public void actionPerformed(ActionEvent ev) {
					saveHiddenList(mainContainer, "HiddenCommunities");
					isShowHide = false;
					showCommunities(f);
				}
			});
			// f.addComponent(BorderLayout.SOUTH, ok);
			// f.putClientProperty("OKButton", ok);

			ok.setUIID("DialogButtonCommand");
			Container fc = new Container(new FlowLayout(Component.CENTER));
			fc.addComponent(ok);
			ok.setPreferredW((int) Math.round((double) f.getPreferredW() / 1.2));
			fc.setUIID("DialogCommandArea");

			f.addComponent(BorderLayout.SOUTH, fc);
			f.putClientProperty("OKButton", fc);

		}
		f.addComponent(BorderLayout.CENTER, mainContainer);

		f.revalidate();
	}

	private void saveList(Container c, String name) {
		Vector<String> vec = new Vector<String>();
		int num = c.getComponentCount();
		MultiButton mb;
		for (int i = 0; i < num; i++) {
			mb = (MultiButton) c.getComponentAt(i);
			vec.add(mb.getTextLine2());
		}

		if (name.equals("Users"))
			listUsers = new Vector<String>(vec);
		else
			listCommunities = new Vector<String>(vec);

		// myLog(vec);
		Storage.getInstance().writeObject(name, vec);
	}

	private void saveHiddenList(Container c, String name) {
		Vector<String> vec = new Vector<String>();
		int num = c.getComponentCount();
		MultiButton mb;
		for (int i = 0; i < num; i++) {
			mb = (MultiButton) c.getComponentAt(i);
			if (!mb.isSelected()) {
				String uname = mb.getTextLine2();
				if (name.equals("HiddenUsers")) {
					if (owner_users_list.keySet().contains(uname) || !lj_users_list.keySet().contains(uname)) {
						owner_users_list.remove(uname);
						listUsers.remove(uname);
						lj_users_list.remove(uname);

						Storage.getInstance().writeObject("owner_users_list", owner_users_list);
						Storage.getInstance().writeObject("Users", listUsers);
					} else {
						vec.add(uname);
					}
				} else {
					if (owner_communities_list.keySet().contains(uname)
							|| !lj_communities_list.keySet().contains(uname)) {
						owner_communities_list.remove(uname);
						listCommunities.remove(uname);
						lj_communities_list.remove(uname);

						Storage.getInstance().writeObject("owner_communities_list", owner_communities_list);
						Storage.getInstance().writeObject("Communities", listCommunities);
					} else {
						vec.add(uname);
					}
				}

			}
		}

		if (name.equals("HiddenUsers")) {
			listHiddenUsers = new Vector<String>(vec);
		} else {
			listHiddenCommunities = new Vector<String>(vec);
		}

		// myLog(vec);
		Storage.getInstance().writeObject(name, vec);
	}

	private Vector<String> loadList(String name) {
		@SuppressWarnings("unchecked")
		Vector<String> vec = (Vector<String>) Storage.getInstance().readObject(name);

		return vec;
		// Storage.getInstance().writeObject(name, vec);
	}

	private void showUsers(final MForm f) {
		savedItemId = null;
		// f.removeAll();
		if (f.getClientProperty("OKButton") != null) {
			f.removeComponent((Component) f.getClientProperty("OKButton"));
		}

		if (listUsers != null && listUsers.size() > 0) {
			Vector<String> newUsersList = new Vector<String>(lj_users_list.keySet());
			newUsersList.removeAll(listUsers);
			listUsers.addAll(newUsersList);
		} else {
			listUsers = new Vector<String>(lj_users_list.keySet());
		}

		if (listHiddenUsers != null && listHiddenUsers.size() > 0) {
			listUsers.removeAll(listHiddenUsers);
			if (isShowHide) {
				listUsers.addAll(listHiddenUsers);
			}
		}

		listUsers = new Vector<String>(new LinkedHashSet<String>(listUsers));

		f.setCustomTitle("Блогеры");

		// f.removeAllCommands();
		f.removeAllCustomCommands();
		// ((CustomSideMenuBar) f.getMenuBar()).removeAllSideCommands();

		Command exit = new Command("Выйти") {
			public void actionPerformed(ActionEvent ev) {

				if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
					Display.getInstance().exitApplication();
				}

			}
		};
		// placeCommand(exit,"right");

		f.setBackCommand(exit);
		// myLog(((Form)f).getCommandCount());

		// for(int i=0;i<f.getCommandCount();i++){
		// myLog(f.getCommand(i));
		// }
		// addMainCommands(f);
		// f.revalidate();
		Command addComm = new Command("Добавить", theme.getImage("1_add_new.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				addJournalDialog("", "");
				// isUsers = false;
				// if (communitiesForm == null) {
				// communitiesForm = newMyForm("Топ", null);
				// //
				// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				// }
				//
				// communitiesForm.show();
				// showFriends(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(addComm);

		Command friends = new Command("Друзья", theme.getImage("1_friends.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {
				isTop = false;
				isFriends = true;
				isUsers = false;
				isSaved = false;

				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showFriends(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Топ", theme.getImage("1_top_users.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				isTop = true;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
					// communitiesForm.setTransitionInAnimator(CommonTransitions.createEmpty());
				}

				communitiesForm.show();
				showTop(communitiesForm);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};
		f.addCustomCommand(topUsers);

		Command communities = new Command("Сообщества", theme.getImage("6_social_person.png")) {
			public void actionPerformed(ActionEvent ev) {
				// if (!isBusy) {

				isTop = false;
				isFriends = false;
				isUsers = false;
				isSaved = false;

				showCommunities(f);
				// findMainContainer(f).scrollComponentToVisible(findMainContainer(f).getComponentAt(0));
				// }
			}
		};

		f.addCustomCommand(communities);

		// communities.putClientProperty("TitleCommand", Boolean.TRUE);
		// placeCommand(communities,"right");
		// communities.putClientProperty("TitleCommand", Boolean.TRUE);

		// communities.putClientProperty("place", "side");
		// f.addCommand(communities);
		//

		final Container mainContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS)) {
			@Override
			protected void dragExit(Component dragged) {
				if (dragged instanceof MultiButton) {
					((MultiButton) dragged).setIcon(null);
				}
			}
		};

		mainContainer.setScrollableY(true);
		mainContainer.setDropTarget(false);

		for (final String uname : listUsers) {
			if (lj_users_list.get(uname) == null) {
				continue;
			}
			// myLog(uname + " " +
			// lj_communities_list.get(uname).get("name"));
			if (!isShowHide) {
				final MultiButton mb = new MultiButton() {
					@Override
					public void longPointerPress(int x, int y) {
						longPressed = true;
						Container parent = this.getParent();
						parent.removeComponent(this);
						parent.addComponent(0, this);
						if (parent.getComponentCount() > 0)
							parent.scrollRectToVisible(0, 0, 1, parent.getHeight(), parent.getComponentAt(0));
						parent.revalidate();
						saveList(mainContainer, "Users");
						// myLog("isDraggable " +
						// this.isDraggable());
						// myLog("LongPress "+x+" "+y);
						// this.setDraggable(true);
						// f.pointerReleased(x, y);
						// f.pointerPressed(x, y);
						// this.setIcon(theme.getImage("5_content_import_export.png"));
						// this.setIconUIID(this.getEmblemUIID());
						// this.revalidate();
						// Display.getInstance().vibrate(100);
						longPressed = false;

					}

				};

				// mb.addDropListener(new ActionListener() {
				//
				// public void actionPerformed(final ActionEvent evt) {
				// Display.getInstance().callSerially(new Runnable() {
				// public void run() {
				// // myLog("Dropped");
				// evt.getDraggedComponent().setDraggable(false);
				// ((MultiButton) evt.getDraggedComponent()).setIcon(null);
				// saveList(mainContainer, "Users");
				// }
				// });
				// }
				// });

				// mb.setTextLine3(entry.getKey() + "3");
				// mb.setTextLine4(entry.getKey() + "4");

				// mb.setMaskName("maskImage");
				// mb.setIconUIID("Avatar");
				// mb.setIcon(r.getImage(C_AVATAR[iter]));
				// final int current = iter;

				mb.setCommand(new Command("") {
					public void actionPerformed(ActionEvent ev) {
						// tagsList.clear();
						// itemsListRecent.clear();
						if (!longPressed) {
							mb.setIcon(null);
							selectedID = "";

							lj_journal_name = uname;
							m_lj_journal_name = StringUtil.replaceAll(uname, "-", "_");
							selectedTag = "";
							clickedItem = null;
							clickedTagItem = null;
							isTags = false;
							// selectedURL = "http://" + lj_journal_name +
							// ".livejournal.com/";
							selectedURL = "http://m.livejournal.com/read/user/" + m_lj_journal_name + "/";
							showTagsForm(f);
						}
					}
				});

				mb.setEmblem(null);
				mb.setHorizontalLayout(false);
				mb.setTextLine2(uname);
				mb.setTextLine1(lj_users_list.get(uname).get("name"));
				mainContainer.addComponent(mb);

			} else {
				final MultiButton mb = new MultiButton();
				mb.setEmblem(null);
				mb.setHorizontalLayout(false);
				mb.setTextLine2(uname);
				mb.setTextLine1(lj_users_list.get(uname).get("name"));
				mb.setCheckBox(true);
				if (listHiddenUsers != null && listHiddenUsers.contains(uname))
					mb.setSelected(false);
				else
					mb.setSelected(true);
				mb.revalidate();
				mainContainer.addComponent(mb);
			}
		}
		// if (clickedUser != null && !isUsers) {
		// // myLog(clickedTagItem);
		// findMainContainer(f).scrollComponentToVisible(clickedUser);
		// }
		if (isShowHide) {
			Button ok = new Button(new Command("ОК") {
				public void actionPerformed(ActionEvent ev) {
					saveHiddenList(mainContainer, "HiddenUsers");
					isShowHide = false;
					showUsers(f);
				}
			});

			// f.addComponent(BorderLayout.SOUTH, ok);
			// f.putClientProperty("OKButton", ok);
			ok.setUIID("DialogButtonCommand");
			Container fc = new Container(new FlowLayout(Component.CENTER));
			fc.addComponent(ok);
			ok.setPreferredW((int) Math.round((double) f.getPreferredW() / 1.2));
			fc.setUIID("DialogCommandArea");

			f.addComponent(BorderLayout.SOUTH, fc);
			f.putClientProperty("OKButton", fc);
		}

		f.addComponent(BorderLayout.CENTER, mainContainer);

		f.revalidate();
	}

	private void savePage(String html, String pageURL, MyItem curItem) {

		pageURL = StringUtil.replaceAll(pageURL, "-", "_");
		isSaving = true;
		isErrorShowed = false;
		RE pattern = new RE("<img.*?src=[\"'](http.*?)[\"'].*?>", RE.MATCH_CASEINDEPENDENT);
		ArrayList<String> images = new ArrayList<String>();
		// final HashMap<String, byte[]> imgData = new HashMap<String,
		// byte[]>();
		final String[] page = new String[1];
		int i = 0;
		// RE patternH = new RE("<.*?>");
		String ext = null;
		while (pattern.match(html, i)) {
			String[] urlParts = Util.split(pattern.getParen(1), ".");
			if (urlParts.length > 0) {
				ext = urlParts[urlParts.length - 1];
				if (ext.length() > 3 || mimeMap.containsKey(ext.toUpperCase()))
					images.add(pattern.getParen(1));
			}
			// images.add(patternH.subst(pattern.getParen(1), "").trim());
			i = pattern.getParenEnd(0);
		}
		// log(images);
		final int numImages = images.size();
		page[0] = html;
		myLog("numImages " + numImages);

		// int displayWidth = Display.getInstance().getDisplayWidth();
		// int displayHeight = Display.getInstance().getDisplayHeight();
		// InfiniteProgress ip = new InfiniteProgress();
		// Dialog dlg = ip.showInifiniteBlocking();
		// dlg.getContentPane().setPreferredW((int) Math.round(displayWidth -
		// (double) displayWidth / 4));
		// dlg.getContentPane().setPreferredH((int) Math.round((double)
		// displayHeight / 4));
		// dlg.setWidth((int) Math.round(displayWidth - (double) displayWidth /
		// 4));
		// dlg.setHeight((int) Math.round((double) displayHeight / 4));

		Dialog dlg = new Dialog("Загрузка");
		dlg.setLayout(new BorderLayout());
		dlg.getContentPane().setUIID("DialogContentPane");
		// dlg.setUIID("CustomDialog");
		Slider slider = new Slider();
		slider.setMaxValue(numImages);
		slider.setMinValue(0);
		slider.setEditable(false);
		Container by = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		by.setUIID("DialogContentPane");
		by.addComponent(slider);

		dlg.addComponent(BorderLayout.CENTER, by);
		Container bc = new Container(new FlowLayout(Component.CENTER));
		bc.setUIID("DialogCommandArea");

		Button cancel = new Button(new Command("Отменить") {
			public void actionPerformed(ActionEvent ev) {
				isErrorShowed = true;
			}
		});

		cancel.setUIID("DialogButtonCommand");
		bc.addComponent(cancel);
		// cancel.setPreferredW(dlg.getDialogComponent().getPreferredW() / 2);
		// Container fc = new Container(new FlowLayout(Component.CENTER));
		// fc.addComponent(bc);
		dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
		dlg.revalidate();
		dlg.showPacked(BorderLayout.CENTER, false);

		// Hashtable themeProps = new Hashtable();
		// themeProps.put("Dialog.transparency", "0");
		// themeProps.put("Dialog.bgColor", "00000");
		// UIManager.getInstance().addThemeProps(themeProps);
		int progr = 0;
		final ImageIO imio = Display.getInstance().getImageIO();
		for (final String item : images) {
			slider.setText((int) Math.round((double) 100 * progr / (double) numImages) + "%");
			slider.setProgress(progr);

			if (isErrorShowed)
				break;
			MyConnectionRequest requestElement = new MyConnectionRequest() {

				// InfiniteProgress ip = new InfiniteProgress();
				// Dialog dlg = ip.showInifiniteBlocking();

				EncodedImage img;

				@Override
				protected void postResponse() {
					// log("Register postResponse");

					if (img != null) {
						// eimg.scale(300, 300);
						int imgWidth = img.getWidth();
						int imgHeight = img.getHeight();
						int newHeight = imgHeight;
						int newWidth = imgWidth;
						float koef;
						if (numImages >= 150)
							koef = 2f;
						else if (numImages >= 100)
							koef = 1.3f;
						else
							koef = 1f;
						myLog(item + " " + imgWidth + "x" + imgHeight);
						// imgData.put("item", img);
						if (imgWidth > imgHeight) {

							if (imgWidth > 100) {
								newWidth = (int) Math.round(Math.sqrt(8000000 / numImages) / koef);
								if (newWidth > imgWidth)
									newWidth = imgWidth;

								if (newWidth < 100)
									newWidth = 100;
								else if (newWidth > 1000)
									newWidth = 1000;
								// img = img.scaledWidth(newWidth);
								newHeight = (int) Math.round((double) newWidth / (double) imgWidth * imgHeight);
								// log(newHeight+" "+newWidth+" "+imgHeight);
							}

						} else {
							if (imgHeight > 100) {
								newHeight = (int) Math.round(Math.sqrt(8000000 / numImages) / koef);
								if (newHeight > imgHeight)
									newHeight = imgHeight;

								if (newHeight < 100)
									newHeight = 100;
								else if (newHeight > 1000)
									newHeight = 1000;

								// img = img.scaledHeight(newHeight);
								newWidth = (int) Math.round((double) newHeight / (double) imgHeight * imgWidth);
							}

						}
						// String[] urlParts = Util.split(item, ".");
						// String ext = null;
						// if (urlParts.length > 0)
						// ext = urlParts[urlParts.length - 1];

						// if (ext != null) {
						// log(newWidth + "x" + newHeight);

						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ByteArrayInputStream is = new ByteArrayInputStream(img.getImageData());

						try {
							imio.save(is, os, ImageIO.FORMAT_JPEG, newWidth, newHeight, 0.7f);
							Util.cleanup(is);
							String encodedImg = WebBrowser.createDataURI(os.toByteArray(), mimeMap.get("JPG"));
							Util.cleanup(os);

							RE pattern = new RE("src=[\"']" + item + "[\"']", RE.MATCH_CASEINDEPENDENT);
							page[0] = pattern.subst(page[0], "src=\"" + encodedImg + "\"", RE.REPLACE_ALL);
							// log(page.get(pageURL));
						} catch (IOException e) {
							e.printStackTrace();
						}
						// img.dispose();
						// }

					}
				}

				protected void readResponse(InputStream input) throws IOException {

					try {
						img = EncodedImage.create(input);

					} catch (IOException ex) {
						ex.printStackTrace();
					}
					Util.cleanup(input);

				}

				// @Override
				// protected void handleException(Exception err) {
				//
				// err.printStackTrace();
				// Dialog.show("", "Не удалось сохранить страницу.", "OK",
				// null);
				// }
				//
				// @Override
				// protected void handleErrorResponseCode(int code, String
				// message) {
				//
				// log(message + " " + code);
				// Dialog.show("Ошибка " + code,
				// "Не удалось сохранить страницу.", "OK", null);
				// }
				//
				// @Override
				// protected void handleRuntimeException(RuntimeException err) {
				//
				// err.printStackTrace();
				// Dialog.show("", "Не удалось сохранить страницу.", "OK",
				// null);
				// }
			};

			requestElement.setUrl(item);
			requestElement.removeAllArguments();
			requestElement.setPost(false);
			isNetworkError = false;
			NetworkManager.getInstance().addToQueueAndWait(requestElement);
			progr++;
		}

		// log(page.get(pageURL));
		// Storage.getInstance().writeObject(pageURL, page.get(pageURL));
		if (!isErrorShowed) {
			try {
				OutputStream out = Storage.getInstance().createOutputStream(pageURL);
				// OutputStreamWriter op = new OutputStreamWriter(out);
				out.write(page[0].getBytes("UTF-8"));
				// Util.writeUTF(page.get(pageURL), (DataOutputStream) out);
				Util.cleanup(out);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			String[] nameAndId = Util.split(pageURL, ".");
			if (nameAndId.length > 2)
				nameAndId[1] = nameAndId[1] + "." + nameAndId[2];
			HashMap<String, HashMap<String, String>> sItems;
			sItems = savedItems.get(nameAndId[0]);
			HashMap<String, String> h = new HashMap<String, String>();

			h.put("name", curItem.getName());
			h.put("author", StringUtil.replaceAll(curItem.getAuthor(), "-", "_"));
			h.put("community", StringUtil.replaceAll(curItem.getCommunity(), "-", "_"));
			h.put("id", curItem.getId());
			h.put("date", curItem.getDate());

			if (sItems != null) {
				if (!sItems.containsKey(nameAndId[1])) {
					sItems.remove(nameAndId[1]);
					sItems.put(nameAndId[1], h);
				}
			} else {
				sItems = new LinkedHashMap<String, HashMap<String, String>>();

				sItems.put(nameAndId[1], h);
			}
			savedItems.put(nameAndId[0], sItems);
			Storage.getInstance().writeObject("savedItems", savedItems);
		}
		// log(savedItems);
		// if (globalDlg != null)
		// globalDlg.dispose();
		isSaving = false;
		if (dlg != null)
			dlg.dispose();

	}

	// private void savePage(String html, final String pageURL, final MyItem
	// curItem) {
	//
	// myLog("Threads: " + NetworkManager.getInstance().getThreadCount());
	// isSaving = true;
	// isErrorShowed = false;
	// isMemoryErrorShowed = false;
	// RE pattern = new RE("<img.*?src=[\"'](http.*?)[\"'].*?>",
	// RE.MATCH_CASEINDEPENDENT);
	// ArrayList<String> images = new ArrayList<String>();
	// // final HashMap<String, byte[]> imgData = new HashMap<String,
	// // byte[]>();
	// final String[] page = new String[1];
	// int i = 0;
	// // RE patternH = new RE("<.*?>");
	// String ext = null;
	// while (pattern.match(html, i)) {
	// String[] urlParts = Util.split(pattern.getParen(1), ".");
	// if (urlParts.length > 0) {
	// ext = urlParts[urlParts.length - 1];
	// if (ext.length() > 3 || mimeMap.containsKey(ext.toUpperCase()))
	// images.add(pattern.getParen(1));
	// }
	// // images.add(patternH.subst(pattern.getParen(1), "").trim());
	// i = pattern.getParenEnd(0);
	// }
	// // myLog(images);
	// final int numImages = images.size();
	// page[0] = html;
	// myLog("numImages " + numImages);
	//
	// if (numImages <= 0) {
	// saveToDisk(pageURL, page[0], curItem);
	// } else {
	// // int displayWidth = Display.getInstance().getDisplayWidth();
	// // int displayHeight = Display.getInstance().getDisplayHeight();
	// // InfiniteProgress ip = new InfiniteProgress();
	// // Dialog dlg = ip.showInifiniteBlocking();
	// // dlg.getContentPane().setPreferredW((int) Math.round(displayWidth
	// // -
	// // (double) displayWidth / 4));
	// // dlg.getContentPane().setPreferredH((int) Math.round((double)
	// // displayHeight / 4));
	// // dlg.setWidth((int) Math.round(displayWidth - (double)
	// // displayWidth /
	// // 4));
	// // dlg.setHeight((int) Math.round((double) displayHeight / 4));
	//
	// final Dialog dlg = new Dialog("Загрузка");
	// dlg.setLayout(new BorderLayout());
	// dlg.getContentPane().setUIID("DialogContentPane");
	// // dlg.setUIID("CustomDialog");
	// final Slider slider = new Slider();
	// slider.setMaxValue(numImages);
	// slider.setMinValue(0);
	// slider.setEditable(false);
	// Container by = new Container(new BoxLayout(BoxLayout.Y_AXIS));
	// by.setUIID("DialogContentPane");
	// by.addComponent(slider);
	//
	// dlg.addComponent(BorderLayout.CENTER, by);
	// Container bc = new Container(new FlowLayout(Component.CENTER));
	// bc.setUIID("DialogCommandArea");
	//
	// Button cancel = new Button(new Command("Отменить") {
	// public void actionPerformed(ActionEvent ev) {
	// isErrorShowed = true;
	// }
	// });
	//
	// cancel.setUIID("DialogButtonCommand");
	// bc.addComponent(cancel);
	// // cancel.setPreferredW(dlg.getDialogComponent().getPreferredW() /
	// // 2);
	// // Container fc = new Container(new FlowLayout(Component.CENTER));
	// // fc.addComponent(bc);
	// dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
	// dlg.revalidate();
	// dlg.showPacked(BorderLayout.CENTER, false);
	//
	// // Hashtable themeProps = new Hashtable();
	// // themeProps.put("Dialog.transparency", "0");
	// // themeProps.put("Dialog.bgColor", "00000");
	// // UIManager.getInstance().addThemeProps(themeProps);
	// final int[] progress = { 0 };
	// final String[] flag = { "" };
	// NetworkManager.getInstance().setThreadCount(10);
	// ActionListener listener = new ActionListener() {
	// public void actionPerformed(ActionEvent evt) {
	//
	// NetworkEvent ne = (NetworkEvent) evt;
	// // if (ne.getConnectionRequest().equals(req) == false) {
	// // return;
	// // }
	//
	// if (ne.getError() != null) {
	// myLog("Error downloading image file");
	// // myLog(ne.getError());
	// // Util.cleanup(is);
	// } else if (ne.getProgressType() == NetworkEvent.PROGRESS_TYPE_COMPLETED)
	// {
	// synchronized (progress) {
	// progress[0]++;
	// }
	// slider.setText((int) Math.round((double) 100 * progress[0] / (double)
	// numImages) + "%");
	// slider.setProgress(progress[0]);
	// // Display.getInstance().callSerially(new Runnable() {
	// // public void run() {
	// //
	// // }
	// // });
	//
	// myLog("Done uploading image file " + (int) Math.round((double) 100 *
	// progress[0] / (double) numImages) + "% Threads "
	// + NetworkManager.getInstance().getThreadCount());
	// Util.cleanup(ne.getConnectionRequest());
	// // Util.cleanup(is);
	// if (isErrorShowed) {
	// synchronized (flag) {
	// if (!"done".equals(flag[0])) {
	// flag[0] = "done";
	// // Enumeration<ConnectionRequest> requests =
	// // NetworkManager.getInstance().enumurateQueue();
	// NetworkManager.getInstance().removeProgressListener(this);
	// Enumeration e = NetworkManager.getInstance().enumurateQueue();
	// NetworkManager.getInstance().shutdown();
	// while (e.hasMoreElements()) {
	// ConnectionRequest rq = (ConnectionRequest) e.nextElement();
	// if (rq != null)
	// rq.kill();
	// // NetworkManager.getInstance().killAndWait(rq);
	// }
	//
	// // NetworkManager.getInstance().start();
	// NetworkManager.getInstance().setThreadCount(1);
	// if (dlg != null)
	// dlg.dispose();
	//
	// if (isMemoryErrorShowed) {
	// Dialog.show("Ошибка", "Сохранить не удалось", "OK", null);
	// }
	// }
	// }
	// }
	// if (progress[0] >= numImages) {//
	// NetworkManager.getInstance().isQueueIdle()
	// NetworkManager.getInstance().removeProgressListener(this);
	// NetworkManager.getInstance().setThreadCount(1);
	// // myLog(page.get(pageURL));
	// // Storage.getInstance().writeObject(pageURL,
	// // page.get(pageURL));
	// if (!isErrorShowed) {
	// saveToDisk(pageURL, page[0], curItem);
	// }
	// isSaving = false;
	// if (dlg != null)
	// dlg.dispose();
	//
	// }
	// }
	// }
	// };
	//
	// NetworkManager.getInstance().addProgressListener(listener);
	// final ImageIO imio = Display.getInstance().getImageIO();
	// for (final String item : images) {
	//
	// if (isErrorShowed)
	// break;
	// MyConnectionRequest requestElement = new MyConnectionRequest() {
	//
	// // InfiniteProgress ip = new InfiniteProgress();
	// // Dialog dlg = ip.showInifiniteBlocking();
	//
	// EncodedImage img;
	//
	// @Override
	// protected void postResponse() {
	// // myLog("Register postResponse");
	// if (isErrorShowed)
	// return;
	//
	// if (img != null) {
	// // eimg.scale(300, 300);
	// int imgWidth = img.getWidth();
	// int imgHeight = img.getHeight();
	// int newHeight = imgHeight;
	// int newWidth = imgWidth;
	// float koef;
	// if (numImages >= 150)
	// koef = 2f;
	// else if (numImages >= 100)
	// koef = 1.3f;
	// else
	// koef = 1f;
	// myLog(item + " " + imgWidth + "x" + imgHeight);
	// // imgData.put("item", img);
	// if (imgWidth > imgHeight) {
	//
	// if (imgWidth > 100) {
	// newWidth = (int) Math.round(Math.sqrt(8000000 / numImages) / koef);
	// if (newWidth > imgWidth)
	// newWidth = imgWidth;
	//
	// if (newWidth < 100)
	// newWidth = 100;
	// else if (newWidth > 1000)
	// newWidth = 1000;
	// // img = img.scaledWidth(newWidth);
	// newHeight = (int) Math.round((double) newWidth / (double) imgWidth *
	// imgHeight);
	// // myLog(newHeight+" "+newWidth+" "+imgHeight);
	// }
	//
	// } else {
	// if (imgHeight > 100) {
	// newHeight = (int) Math.round(Math.sqrt(8000000 / numImages) / koef);
	// if (newHeight > imgHeight)
	// newHeight = imgHeight;
	//
	// if (newHeight < 100)
	// newHeight = 100;
	// else if (newHeight > 1000)
	// newHeight = 1000;
	//
	// // img = img.scaledHeight(newHeight);
	// newWidth = (int) Math.round((double) newHeight / (double) imgHeight *
	// imgWidth);
	// }
	//
	// }
	// // String[] urlParts = Util.split(item, ".");
	// // String ext = null;
	// // if (urlParts.length > 0)
	// // ext = urlParts[urlParts.length - 1];
	//
	// // if (ext != null) {
	// // myLog(newWidth + "x" + newHeight);
	//
	// ByteArrayOutputStream os = new ByteArrayOutputStream();
	// ByteArrayInputStream is = new ByteArrayInputStream(img.getImageData());
	//
	// if (!isErrorShowed) {
	// try {
	// synchronized (imio) {
	// imio.save(is, os, ImageIO.FORMAT_JPEG, newWidth, newHeight, 0.7f);
	// }
	// Util.cleanup(is);
	// String encodedImg = WebBrowser.createDataURI(os.toByteArray(),
	// mimeMap.get("JPG"));
	// Util.cleanup(os);
	//
	// RE pattern = new RE("src=[\"']" + item + "[\"']",
	// RE.MATCH_CASEINDEPENDENT);
	// synchronized (page) {
	// page[0] = pattern.subst(page[0], "src=\"" + encodedImg + "\"",
	// RE.REPLACE_ALL);
	// }
	//
	// // myLog(page.get(pageURL));
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// img.dispose();
	//
	// // progress++;
	// }
	// // }
	//
	// }
	// }
	//
	// protected void readResponse(InputStream input) throws IOException {
	// if (!isErrorShowed) {
	// try {
	// img = EncodedImage.create(input);
	//
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// } catch (OutOfMemoryError err) {
	// err.printStackTrace();
	// isErrorShowed = true;
	// isMemoryErrorShowed = true;
	// }
	// }
	// Util.cleanup(input);
	//
	// }
	//
	// };
	//
	// requestElement.setUrl(item);
	// requestElement.removeAllArguments();
	// requestElement.setPost(false);
	// isNetworkError = false;
	// NetworkManager.getInstance().addToQueue(requestElement);
	//
	// }
	// }
	// // while (!NetworkManager.getInstance().isQueueIdle()) {
	// // if (isErrorShowed){
	// //
	// // Enumeration requests=NetworkManager.getInstance().enumurateQueue();
	// // while (requests.hasMoreElements()){
	// //
	// NetworkManager.getInstance().killAndWait((ConnectionRequest)requests.nextElement());
	// // }
	// //
	// // }
	// //
	// // }
	//
	// }
	//
	// public void saveToDisk(String pageURL, String page, MyItem curItem) {
	// try {
	// OutputStream out = Storage.getInstance().createOutputStream(pageURL);
	// // OutputStreamWriter op = new
	// // OutputStreamWriter(out);
	// out.write(page.getBytes("UTF-8"));
	// // Util.writeUTF(page.get(pageURL),
	// // (DataOutputStream) out);
	// Util.cleanup(out);
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// }
	//
	// String[] nameAndId = Util.split(pageURL, ".");
	// if (nameAndId.length > 2)
	// nameAndId[1] = nameAndId[1] + "." + nameAndId[2];
	// HashMap<String, HashMap<String, String>> sItems;
	// sItems = savedItems.get(nameAndId[0]);
	// HashMap<String, String> h = new HashMap<String, String>();
	//
	// h.put("name", curItem.getName());
	// h.put("author", curItem.getAuthor());
	// h.put("id", curItem.getId());
	// h.put("date", curItem.getDate());
	//
	// if (sItems != null) {
	// if (!sItems.containsKey(nameAndId[1])) {
	// sItems.remove(nameAndId[1]);
	// sItems.put(nameAndId[1], h);
	// }
	// } else {
	// sItems = new LinkedHashMap<String, HashMap<String, String>>();
	//
	// sItems.put(nameAndId[1], h);
	// }
	// savedItems.put(nameAndId[0], sItems);
	// Storage.getInstance().writeObject("savedItems", savedItems);
	//
	// // myLog(savedItems);
	// // if (globalDlg != null)
	// // globalDlg.dispose();
	//
	// }

	// static public Component getComponentByName(Container c, String name) {
	// Component found=null;
	// if (c != null)
	// for (int i = 0; i < c.getComponentCount(); i++) {
	// myLog(c.getComponentAt(i).getClientProperty("CustomID") +
	// " " + i);
	// if (c.getComponentAt(i).getClientProperty("CustomID")!=null &&
	// c.getComponentAt(i).getClientProperty("CustomID").equals(name)) {
	// //myLog(c);
	// found=c.getComponentAt(i);
	// } else if (c.getComponentAt(i) instanceof Container) {
	// found=getComponentByName((Container) c.getComponentAt(i), name);
	// }
	// if (found!=null)
	// break;
	// }
	//
	// return found;
	// }
}
