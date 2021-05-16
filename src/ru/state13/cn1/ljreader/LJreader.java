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


	@SuppressWarnings("unchecked")
	public void init(Object context) {

		isIOS = "ios".equals(Display.getInstance().getPlatformName());
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

		user_login = (Vector<String>) Storage.getInstance().readObject("user_login");

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
	}

	public void start() {
		if (current != null) {
			current.show();
			return;
		} else {
			
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

			if (((SideMenuBar) communitiesForm.getMenuBar()).getCommandCount() <= 0)
				addMainCommands(communitiesForm);

			postMain(communitiesForm);
		}

	}

	public void stop() {
		current = Display.getInstance().getCurrent();
	}

	public void destroy() {
	}

	private void addTags(final Container c, final ArrayList<SimpleEntry<String, Integer>> tagsList) {

		c.removeAll();
		if (tagsList.size() > 0) {
			
			for (SimpleEntry<String, Integer> entry : tagsList) {
				MultiButton mb = new MultiButton();
				mb.setEmblem(null);
				mb.setHorizontalLayout(true);
				mb.setTextLine1(entry.getKey());
				if (entry.getValue() != 0)
					mb.setTextLine2(entry.getValue().toString());

				final SimpleEntry<String, Integer> e = entry;
				mb.setCommand(new Command("") {
					public void actionPerformed(ActionEvent ev) {
						
						selectedURL = "http://m.livejournal.com/read/user/" + m_lj_journal_name + "/";
						selectedTag = e.getKey();
						clickedItem = null;
						totalEntr = 10000;
						if (e.getValue() != 0)
							selectedTagItems = e.getValue();
						else
							selectedTagItems = 10000;
						showItemsForm((MForm) c.getComponentForm());
						
					}
				});

				c.addComponent(mb);

				if (entry.getKey() == selectedTag) {
					clickedTagItem = mb;
				}

			}

		} else {
			Dialog.show(templateErrorTitle, templateError, "OK", null);
			
		}
		isBusy = false;
	}

	private void sortTags(final Container c, final ArrayList<SimpleEntry<String, Integer>> tagsList) {
		if (tagsList.size() > 0) {
			if (!tagsSortedA && tagsSortA) {

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

			} else if (tagsSortedA && !tagsSortA) {

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

			}
		}
	}

	private void setTagCommands(final Container c) {
		((MForm) c.getComponentForm()).removeAllCustomCommands();

		if (!tagsSortA) {
			Command comSort = new Command("А-я", theme.getImage("4_collections_sort_by_size.png")) {
				public void actionPerformed(ActionEvent ev) {
					clickedItem = null;
					tagsSortA = true;
					sortTags(c, tagsListAll.get(selectedURL) == null ? new ArrayList<SimpleEntry<String, Integer>>()
							: tagsListAll.get(selectedURL));
				}
			};

			((MForm) c.getComponentForm()).addCustomCommand(comSort);
		} else {
			Command comSort = new Command("0-9", theme.getImage("4_collections_sort_by_size.png")) {
				public void actionPerformed(ActionEvent ev) {
					clickedItem = null;
					tagsSortA = false;
					sortTags(c, tagsListAll.get(selectedURL) == null ? new ArrayList<SimpleEntry<String, Integer>>()
							: tagsListAll.get(selectedURL));
				
				}
			};
			((MForm) c.getComponentForm()).addCustomCommand(comSort);
		}

		Command com = new Command("Записи", theme.getImage("4_collections_view_as_list.png")) {
			public void actionPerformed(ActionEvent ev) {
				selectedTagItems = 10000;
				isTags = false;
				clickedTagItem = null;
				clickedItem = null;
				showEntries(c);
			}
		};

		((MForm) c.getComponentForm()).addCustomCommand(com);
		((MForm) c.getComponentForm()).setBackCommand(com);
		c.getComponentForm().revalidate();
	}

	private void showTags(final Container c, final ArrayList<SimpleEntry<String, Integer>> tagsList) {
		myLog("showTags");
		totalEntr = 10000;


		if (tagsList.size() <= 0) {
			myLog("Getting tags...");
			
			InfiniteProgress ip = new InfiniteProgress();
			final Dialog dlg = ip.showInifiniteBlocking();

			MyConnectionRequest requestElement = new MyConnectionRequest() {

				@Override
				protected String initCookieHeader(String cookie) {

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

					int i = 0;
					RE pattern;
					if (isFallToSimpleTags || (lj_user.get("tag_num") != null && lj_user.get("tag_num").length() > 0)) {
						pattern = new RE(lj_user.get("tag_start"));
					} else {
						pattern = new RE("<ul class=\"j-w-list j-w-list-tags j-p-tagcloud\">");
					}

					if (pattern.match(response))
						i = pattern.getParenEnd(0);
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

					RE patternH = new RE("(<.*?>)|(&[^;]+?;)");
					SimpleEntry<String, Integer> te;
					for (String s : lj_user.keySet()) {
						System.out.println(s + ":" + lj_user.get(s));
					}
					try {
						while (pattern.match(response, i)) {
							if (lj_user.get("tag_num") == null
									|| (lj_user.get("tag_num") != null && lj_user.get("tag_num").length() > 0)) {
								if (pattern2.match(response, i)) {
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
							i = pattern.getParenEnd(0);

						}

					} catch (Exception ex) {
						ex.printStackTrace();
						if (dlg != null)
							dlg.dispose();
						
					} catch (Error er) {
						er.printStackTrace();
						if (dlg != null)
							dlg.dispose();
						
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
		
			requestElement.setPost(false);
			requestElement.setMyURL(selectedURL);

			isNetworkError = false;
			NetworkManager.getInstance().addToQueue(requestElement);
			

		} else {

			setTagCommands(c);
			addTags(c, tagsList);

			if (c.getComponentCount() > 0)
				c.scrollRectToVisible(0, 0, 1, c.getHeight(), c.getComponentAt(0));

			if (clickedTagItem != null) {
				c.scrollComponentToVisible(clickedTagItem);
			} else {
				c.scrollComponentToVisible(c.getComponentAt(0));
			}
			((MForm) c.getComponentForm()).setCustomTitle(lj_journal_name + " - теги");
		}

	}

	private void showEntries(final Container c) {
		myLog("showEntries");
		c.removeAll();
		totalEntr = 10000;

		((MForm) c.getComponentForm()).setCustomTitle("Записи");

		((MForm) c.getComponentForm()).removeAllCustomCommands();

		Command tags = new Command("Теги", theme.getImage("4_collections_labels.png")) {
			public void actionPerformed(ActionEvent ev) {
				if (!isBusy) {
					isTags = true;
					showTags(c, tagsListAll.get(selectedURL) == null ? new ArrayList<SimpleEntry<String, Integer>>()
							: tagsListAll.get(selectedURL));
				}
			}
		};

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

		((MForm) c.getComponentForm()).addCustomCommand(reload);

		Command back = new Command("Назад") {
			public void actionPerformed(ActionEvent ev) {
				if (selectedTag.length() > 0 && tagsForm != null && !((MForm) c.getComponentForm()).equals(tagsForm)) {
					tagsForm.showBack();
				} else {

					communitiesForm.showBack();
				}
			}
		};
		((MForm) c.getComponentForm()).setBackCommand(back);

		c.getComponentForm().revalidate();

		showItems(c, itemsListAll.get(selectedURL + selectedTag) == null ? new ArrayList<MyItem>()
				: itemsListAll.get(selectedURL + selectedTag));

	}

	private MForm newMyForm(String title, final MForm parent) {
		MForm f = new MForm();
		f.setCustomTitle(title);
		if (parent != null) {
			Command back = new Command(parent.getTitle()) {
				public void actionPerformed(ActionEvent ev) {
					parent.showBack();
					parent.revalidate();
				}
			};
			f.setBackCommand(back);
		} else {
			Command exit = new Command("Выйти") {
				public void actionPerformed(ActionEvent ev) {

					if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
						Display.getInstance().exitApplication();
					}

				}
			};

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
							communitiesForm.showBack();
						}

					} else if (parent.equals(tagsForm)) {
						communitiesForm.showBack();
					} else {
						parent.showBack();
					}
				}
			};
			tagsForm.setBackCommand(back);
		} else {
			Command back = new Command("") {
				public void actionPerformed(ActionEvent ev) {

					communitiesForm.showBack();
				}
			};
			tagsForm.setBackCommand(back);
		}
		Container c = addLayoutY(tagsForm);
		tagsForm.putClientProperty("mainc", c);
		tagsForm.revalidate();
		tagsForm.show();

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

				isUsers = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
				}

				communitiesForm.show();
				if (isRegistered) {
					c.removeAll();
					showItems(c, new ArrayList<MyItem>());
				} else {
					showFriends(communitiesForm);
				}
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Авторизация", theme.getImage("1_lock.png")) {
			public void actionPerformed(ActionEvent ev) {
				login();
			}
		};
		f.addCustomCommand(topUsers);

		Command users = new Command("Блогеры", theme.getImage("6_social_group.png")) {
			public void actionPerformed(ActionEvent ev) {

				isUsers = true;
				showUsers(f);
			}
		};
		f.addCustomCommand(users);
	}

	private void showFriends(final MForm f) {
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
		setFriendsCommands(c);

		communitiesForm.setCustomTitle("Лента");

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
				authorizeUser();
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
		}
		communitiesForm.setCustomTitle("Лента");

	}

	private void login() {
		final Dialog dlg = new Dialog("Ваш аккаунт в ЖЖ");
		dlg.setLayout(new BorderLayout());
		dlg.getContentPane().setUIID("DialogContentPane");
		TextField.setUseNativeTextInput(true);
		final TextField login = new TextField();
		login.setHint("Логин");
		final TextField pass = new TextField();
		pass.setHint("Пароль");
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
		dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
		dlg.revalidate();
		dlg.showPacked(BorderLayout.CENTER, true);
	}

	private void setTopCommands(final Container c) {
		final MForm f = (MForm) c.getComponentForm();
		f.removeAllCustomCommands();

		Command friends = new Command("Друзья", theme.getImage("1_friends.png")) {
			public void actionPerformed(ActionEvent ev) {

				isUsers = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
				}

				communitiesForm.show();
				showFriends(communitiesForm);
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Обновить", theme.getImage("1_navigation_refresh.png")) {
			public void actionPerformed(ActionEvent ev) {

				isUsers = false;

				communitiesForm.show();

				c.removeAll();
				showItems(c, new ArrayList<MyItem>());
			}
		};
		f.addCustomCommand(topUsers);

		Command communities = new Command("Сообщества", theme.getImage("6_social_person.png")) {
			public void actionPerformed(ActionEvent ev) {

				isUsers = false;
				showCommunities(f);
			}
		};

		f.addCustomCommand(communities);
	}

	private void showTop(final MForm f) {
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
		showItems(c, itemsListAll.get(selectedURL) == null ? new ArrayList<MyItem>() : itemsListAll.get(selectedURL));
		communitiesForm.setCustomTitle("Топ");

	}

	private void showItems(final Container c, final ArrayList<MyItem> itemsList) {

		myLog("showItems");

		if (selectedTag.length() > 0)
			((MForm) c.getComponentForm()).setCustomTitle(
					(nameConv.containsKey(lj_journal_name) ? nameConv.get(lj_journal_name) : lj_journal_name) + " - "
							+ selectedTag);
		else
			((MForm) c.getComponentForm()).setCustomTitle(
					(nameConv.containsKey(lj_journal_name) ? nameConv.get(lj_journal_name) : lj_journal_name));
		isBusy = true;
		isFilterAdded = false;

		if ("top".equals(lj_journal_name)) {
			isFriends = false;
			setTopCommands(c);
		} else if ("friends".equals(lj_journal_name)) {
			isTop = false;
			setFriendsCommands(c);
		}
		final int[] fr = { 1 };

		myLog(lj_journal_name);
		if (itemsList.size() > 0) {
			fr[0] = (int) Math.floor((double) itemsList.size() / 10) + 1;
			myLog("Page: " + fr[0]);
		}

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
									selectedID = e.getId();
									selectedItem = e.getName();
									currentMyItem = entry;
									showBrowserForm((MForm) c.getComponentForm());
								}
							});
							buttons[i - itemsCounter] = mb;
							if (entry.getId() == selectedID)
								clickedItem = mb;

						}
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
					Dialog.show(templateErrorTitle, templateError, "OK", null);
				}
				isBusy = false;

				if ("http://m.livejournal.com/read/friends/".equals(selectedURL))
					addFilter();
				c.revalidate();
			}
			public void addFilter() {
				if (!isFilterAdded && friendsFilter != null && friendsFilter.size() > 0) {

					isFilterAdded = true;

					final ComboBox cb = new ComboBox(new Vector<String>(friendsFilter.keySet()));

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
								c.removeAll();
								showItems(c, new ArrayList<MyItem>());
							}
						}
					});
					cb.setPreferredH(cb.getPreferredH() - (int) Math.round(((double) cb.getPreferredH() / 100) * 25));
					if (c.getComponentForm().getClientProperty("FilterContainer") != null)
						((Container) c.getComponentForm().getClientProperty("FilterContainer"))
								.addComponent(BorderLayout.NORTH, cb);
					((MForm) c.getComponentForm()).revalidate();
				}

			}

			public void run() {

				if (itemsList.size() > 0 && itemsCounter == 0) {
					addCmp();
					if (c.getComponentCount() > 0)
						c.scrollRectToVisible(0, 0, 1, c.getHeight(), c.getComponentAt(0));
					if (clickedItem != null) {
						c.scrollComponentToVisible(clickedItem);
					} else {
						c.scrollComponentToVisible(c.getComponentAt(0));
					}

				} else if (itemsList.size() < selectedTagItems) {

					MyConnectionRequest requestElement = new MyConnectionRequest() {
						String response;

						@Override
						protected void postResponse() {
							myLog("Register postResponse showItems");
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
								}
							}

							if ("top".equals(lj_journal_name)) {
								m_user = lj_communities_list.get("top");
							} else {
								m_user = lj_communities_list.get("friends");
							}
							pattern = new RE(m_user.get("item_start"));
							RE patternH = new RE("(<.*?>)|(&[^;]+?;)");
							RE patternMeta = new RE("<p class=\"item-meta\">(.*?)</p>");
							i = 0;
							String nam;
							while (pattern.match(response, i)) {
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
									item.setName("Без темы");
									pattern2 = new RE(m_user.get("item_name"));
									if (pattern2.match(response, i)) {

										if (pattern2.getParen(1).length() > 0) {
											nam = patternH.subst(pattern2.getParen(1), "").trim();
											if (nam.length() > 0)
												item.setName(
														nam.indexOf("- ") == 0 ? nam.substring(2).trim() : nam.trim());
											else
												item.setName("Без темы");
										}
									}
									pattern2 = new RE(m_user.get("item_id"));
									if (pattern2.match(response, i)) {
										item.setId(pattern2.getParen(1));
									}
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
									if (item.getId().length() > 0 && !itemsListIds.contains(item.getId()))
										itemsList.add(item);
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

						}
					};

					if (!isRegistered) {
						requestElement.removeAllArguments();
						requestElement.addRequestHeader("cookie", "rating_show_custom=1; langpref=ru/1469450983;");
					}

					requestElement.setPost(false);
					requestElement.setTag(selectedTag);
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
						rUrl = rUrl + "tag/" + Util.encodeUrl(selectedTag) + "/";
					}
					requestElement.setUrl(rUrl + "p" + fr[0]);
					fr[0]++;

					myLog(requestElement.getUrl());
					isNetworkError = false;
					NetworkManager.getInstance().addToQueue(requestElement);

				} else {
					addCmp();
				}
			}
		});
		c.getComponentForm().revalidate();
	}
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
		} else {
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
							communitiesForm.showBack();
						}

					} else if (parent.equals(itemsForm)) {
						if (isTags) {
							showTagsForm(itemsForm);
						} else {
							communitiesForm.showBack();
						}
					} else {

						parent.showBack();
					}
				}
			};
			itemsForm.setBackCommand(back);
		} else {
			Command back = new Command("") {
				public void actionPerformed(ActionEvent ev) {
					if (isTags) {
						showTagsForm(itemsForm);
					} else {
						communitiesForm.showBack();
					}
				}
			};
			itemsForm.setBackCommand(back);
		}
		final Container c = addLayoutY(itemsForm);
		itemsForm.putClientProperty("mainc", c);
		itemsForm.removeAllCustomCommands();

		Command tags = new Command("Теги", theme.getImage("4_collections_labels.png")) {
			public void actionPerformed(ActionEvent ev) {
				if (!isBusy) {
					isTags = true;
					showTagsForm(itemsForm);
				}
			}
		};
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
						savePage(wbPageOriginal, lj_journal_name + "."
								+ (("top".equals(lj_journal_name) || "friends".equals(lj_journal_name))
										? (currentMyItem.getCommunity().length() > 0 ? currentMyItem.getCommunity()
												: currentMyItem.getAuthor()) + "."
										: "")
								+ selectedID, currentMyItem);
						f.revalidate();
					}

				}
			};
			f.addCustomCommand(save);
		}

		if (BrowserComponent.isNativeBrowserSupported()) {
			if (isPinchToZoom) {
				Command com = new Command("Уменьшение", theme.getImage("9_av_return_from_full_screen.png")) {
					public void actionPerformed(ActionEvent ev) {
						isPinchToZoom = false;
						((BrowserComponent) myWb.getInternal()).setPinchToZoomEnabled(false);
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + wbPage + htmlFooter, null);
						addBrowserCommands(f);

					}
				};
				f.addCustomCommand(com);
			} else {

				Command com = new Command("Увеличение", theme.getImage("9_av_full_screen.png")) {
					public void actionPerformed(ActionEvent ev) {
						isPinchToZoom = true;
						((BrowserComponent) myWb.getInternal()).setPinchToZoomEnabled(true);
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + wbPageOriginal + htmlFooter,
								null);
						addBrowserCommands(f);

					}
				};
				f.addCustomCommand(com);
			}
		}

		Command com = new Command("Комментарии", theme.getImage("6_social_chat.png")) {
			public void actionPerformed(ActionEvent ev) {
				Display.getInstance()
						.execute(
								"http://m.livejournal.com/read/user/"
										+ ("top".equals(lj_journal_name) || "friends".equals(lj_journal_name)
												? (currentMyItem.getCommunity().length() > 0
														? currentMyItem.getCommunity() : currentMyItem.getAuthor())
												: lj_journal_name)
										+ "/" + currentMyItem.getId() + "/comments#comments");
			}
		};
		f.addCustomCommand(com);
		f.revalidate();
	}

	protected void showBrowserPage(MForm f) {
		f.removeAllShowListeners();

		final WebBrowser myWb = (WebBrowser) f.getClientProperty("myWb");

		if (savedItemId == null) {
			InfiniteProgress ip = new InfiniteProgress();
			final Dialog dlg = ip.showInifiniteBlocking();
			MyConnectionRequest requestElement = new MyConnectionRequest() {

				String resp = "";

				@Override
				protected void postResponse() {
					wbPage = "<b>" + selectedItem + "</b><br><br>" + resp;
					if (myWb != null) {
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader()) + wbPage + htmlFooter, null);

					}
					saveEnabled = true;
					if (dlg != null)
						dlg.dispose();
				}

				protected void readResponse(InputStream input) throws IOException {
					String tagStr = "";
					String[] tagStrArr = null;
					String response = Util.readToString(input, "UTF-8");
					Util.cleanup(input);
					String[] responseArr = Util.split(response, "\n");

					resp = "";
					wbPageOriginal = "";

					for (int i = 0; i < responseArr.length; i++) {
						if ("events_1_event".equals(responseArr[i]) && resp.length() == 0) {
							resp = Util.decode(responseArr[i + 1], "UTF-8", false);
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
					if (resp != null && resp.length() > 0) {

						resp = formatHTML(resp);

						wbPageOriginal = "<b>" + selectedItem + "</b><br><br>" + resp
								+ ((tagStr.length() > 0)
										? "<br><br><div style=\"line-height: 2\">Теги: " + tagStr + "</div><br><br>"
										: "");

						resp = adaptHTML(resp) + ((tagStr.length() > 0)
								? "<br><br><div style=\"line-height: 2\">Теги: " + tagStr + "</div><br><br>" : "");
					}

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

			if (currentMyItem.getCommunity().length() > 0)
				requestElement.addArgument("usejournal", currentMyItem.getCommunity());
			else if (currentMyItem.getAuthor().length() > 0)
				requestElement.addArgument("usejournal", currentMyItem.getAuthor());
			else
				requestElement.addArgument("usejournal", lj_journal_name);

			requestElement.addArgument("selecttype", "one");
			requestElement.addArgument("ver", "1");
			requestElement.addArgument("ditemid", selectedID);
			isNetworkError = false;
			NetworkManager.getInstance().addToQueueAndWait(requestElement);
		} else {
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
			} catch (IOException e) {

				wbPageOriginal = "";
				wbPage = "";
				myLog("IOException");
				e.printStackTrace();

				if (dlg != null)
					dlg.dispose();

				Dialog.show("Ошибка", "Файл не найден.", "   OK   ", null);
			}

		}
		firstPage = myWb.getPage();
	}

	protected void showBrowserForm(final MForm parent) {
		isPinchToZoom = false;
		saveEnabled = false;
		firstPage = null;

		if (browserForm == null) {
			browserForm = newMyForm("Браузер", parent);
		}

		browserForm.removeAll();
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

		Command back = new Command("Назад") {
			public void actionPerformed(ActionEvent ev) {
				parent.showBack();
				parent.revalidate();

				browserForm.removeAll();
				browserForm.putClientProperty("myWb", null);

			}
		};

		browserForm.setBackCommand(back);

		browserForm.addShowListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final WebBrowser myWb;
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
									} else {
										Display.getInstance().execute(url);
										return false;
									}
									return false;
								}
							});
				}
				addBrowserCommands(browserForm);
				addMainCommands(browserForm);

				browserForm.revalidate();
				showBrowserPage(browserForm);

			}
		});

		browserForm.revalidate();

		browserForm.show();

	}

	private String formatHTML(String resp) {

		RE pattern = new RE("\r\n");
		resp = pattern.subst(resp, "<br>", RE.REPLACE_ALL);
		pattern = new RE("\n|\r");
		resp = pattern.subst(resp, "<br>", RE.REPLACE_ALL);
		pattern = new RE("style=[\"'](.*?)[\"']", RE.MATCH_CASEINDEPENDENT);
		resp = pattern.subst(resp, " ", RE.REPLACE_ALL);

		pattern = new RE("<lj-embed.*?>");
		int i = 0;
		String resp1 = new String(resp);
		while (pattern.match(resp, i)) {
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

		pattern = new RE("<lj user=\"(.*?)\".*?>");
		i = 0;
		while (pattern.match(resp, i)) {
			resp1 = StringUtil.replaceAll(resp1, pattern.getParen(0),
					"<a href=\"http://" + pattern.getParen(1) + ".livejournal.com\">" + pattern.getParen(1) + "</a>");
			i = pattern.getParenEnd(0);
		}
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

	protected void authorizeUser() {

		if (isAuthorizing)
			return;

		isAuthorizing = true;

		Cookie.clearCookiesFromStorage();

		ConnectionRequest requestElement = new ConnectionRequest() {

			@Override
			protected void postResponse() {
			}

			protected void readResponse(InputStream input) throws IOException {
				String response = Util.readToString(input, "UTF-8");
				myLog(response);

			}

			@Override
			protected void readHeaders(Object connection) throws IOException {

				String[] he = getHeaderFieldNames(connection);
				for (String h : he) {
					myLog(h + ": " + getHeader(connection, h));
				}
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
		if (user_login != null && user_login.size() > 1) {
			requestElement.addArgument("user", user_login.get(0));
			requestElement.addArgument("password", user_login.get(1));

			requestElement.addArgument("x", "34");
			requestElement.addArgument("y", "19");
			requestElement.addArgument("returnto", "http://m.livejournal.com/read/friends");
			requestElement.addArgument("ret_fail", "http://m.livejournal.com/login?error=");
			requestElement.addArgument("back_uri", "/read/friends");
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
			}

			protected void readResponse(InputStream input) throws IOException {
				String response = Util.readToString(input, "UTF-8");
				responseArr = Util.split(response, "\n");
				myLog(response);

			}
		};

		requestElement.setUrl(lj_flat_url);

		requestElement.removeAllArguments();
		requestElement.setPost(true);

		requestElement.addArgument("mode", "getchallenge");

		NetworkManager.getInstance().addToQueueAndWait(requestElement);

	}

	protected void postMain(MForm f) {

	}

	protected void beforeMain(final MForm f) {

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

				}
				isNetworkError = true;
				evt.consume();
			}
		});

		if (isUsers)
			showUsers(f);
		else
			showCommunities(f);
	}

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
			f.addCommand(c);
		}

		Command users = new Command("Блогеры", theme.getImage("6_social_person_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				isTop = false;
				isFriends = false;
				isUsers = true;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("123", null);
				}

				communitiesForm.show();
				if (((SideMenuBar) f.getMenuBar()).getCommandCount() <= 0)
					addMainCommands(f);
				showUsers(communitiesForm);
			}
		};
		f.addCommand(users);

		Command communities = new Command("Сообщества", theme.getImage("6_social_group_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				isTop = false;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("123", null);
				}

				communitiesForm.show();
				showCommunities(communitiesForm);
			}
		};
		f.addCommand(communities);

		Command top25 = new Command("Топ", theme.getImage("1_top_users_l.png")) {
			public void actionPerformed(ActionEvent ev) {

				isTop = true;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
				}

				communitiesForm.show();
				showTop(communitiesForm);
			}
		};
		f.addCommand(top25);

		Command friends = new Command("Друзья", theme.getImage("1_friends_l.png")) {
			public void actionPerformed(ActionEvent ev) {

				isTop = false;
				isFriends = true;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Друзья", null);
				}

				communitiesForm.show();
				showFriends(communitiesForm);
			}
		};
		f.addCommand(friends);

		Command saved = new Command("Сохраненные", theme.getImage("4_collections_collection_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				isTop = false;
				isFriends = false;
				isUsers = false;
				isSaved = true;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("123", null);
				}

				communitiesForm.show();
				showSavedJournals(communitiesForm);
			}
		};
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
						isNight = false;
						UIManager.getInstance().setThemeProps(theme.getTheme(lightTheme));
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
						if (browserForm != null) {
							browserForm.refreshTheme();
							WebBrowser myWb = (WebBrowser) browserForm.getClientProperty("myWb");

							if (myWb != null) {
								myWb.setPage(getLightHeader() + (isPinchToZoom ? wbPageOriginal : wbPage) + htmlFooter,
										null);
							}
						}
						Storage.getInstance().writeObject("isNight", isNight);
						f.removeAllCommands();
						addMainCommands(f);

					}
				};
				f.addCommand(daynight);
			} else {
				Command daynight = new Command("День", theme.getImage("10_device_access_brightness_high_l.png")) {
					public void actionPerformed(ActionEvent ev) {
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
						if (browserForm != null) {
							browserForm.refreshTheme();
							WebBrowser myWb = (WebBrowser) browserForm.getClientProperty("myWb");

							if (myWb != null) {
								myWb.setPage(getDarkHeader() + (isPinchToZoom ? wbPageOriginal : wbPage) + htmlFooter,
										null);
							}
						}
						Storage.getInstance().writeObject("isNight", isNight);
						f.removeAllCommands();
						addMainCommands(f);
					}
				};
				f.addCommand(daynight);
			}
		}
		Command hide = new Command("Видимость", theme.getImage("3_rating_half_important_l.png")) {
			public void actionPerformed(ActionEvent ev) {
				isShowHide = true;
				communitiesForm.show();
				if (isUsers) {
					showUsers(communitiesForm);
				} else {
					showCommunities(communitiesForm);
				}
			}
		};
		f.addCommand(hide);

		Command fontSize = new Command("Шрифт", theme.getImage("2_action_settings_l.png")) {
			public void actionPerformed(ActionEvent ev) {

				showFontSizeDlg();
				if (browserForm != null) {
					WebBrowser myWb = (WebBrowser) browserForm.getClientProperty("myWb");

					if (myWb != null) {
						myWb.setPage((isNight ? getDarkHeader() : getLightHeader())
								+ (isPinchToZoom ? wbPageOriginal : wbPage) + htmlFooter, null);
					}
				}
			}
		};
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
		savedItemsForm.setBackCommand(back);

		savedItemsForm.removeAllCustomCommands();
		savedItemsForm.revalidate();
		savedItemsForm.show();

		if (savedItems != null && savedItems.get(lj_journal_name) != null
				&& savedItems.get(lj_journal_name).size() > 0) {
			Command del = new Command("Удалить", theme.getImage("5_content_discard.png")) {
				public void actionPerformed(ActionEvent ev) {
					if (!isShowRemove) {
						isShowRemove = true;
						showSavedItems(c);
					} else {
						isShowRemove = false;
						showSavedItems(c);
					}
				}
			};
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
	}

	private void showSavedJournals(final MForm f) {
		if (f.getClientProperty("OKButton") != null) {
			f.removeComponent((Component) f.getClientProperty("OKButton"));
		}

		final Container mainContainer = addLayoutY(f);

		f.setCustomTitle("Сохраненные");
		f.removeAllCustomCommands();

		Command exit = new Command("Выйти") {
			public void actionPerformed(ActionEvent ev) {

				if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
					Display.getInstance().exitApplication();
				}

			}
		};

		f.setBackCommand(exit);

		if (savedItems.size() > 0) {
			Command del = new Command("Удалить", theme.getImage("5_content_discard.png")) {
				public void actionPerformed(ActionEvent ev) {
					if (!isShowRemove) {
						isShowRemove = true;
						showSavedJournals(f);
					} else {
						isShowRemove = false;
						showSavedJournals(f);
					}
				}
			};
			f.addCustomCommand(del);
		}

		for (final String uname : savedItems.keySet()) {
			String uname1 = StringUtil.replaceAll(uname, "_", "-");
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

		Container by = new Container(new BoxLayout(BoxLayout.Y_AXIS));
		by.setUIID("DialogContentPane");
		by.addComponent(ns);
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
		bc.addComponent(ok);
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
		dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
		dlg.revalidate();
		dlg.showPacked(BorderLayout.CENTER, true);

	}

	private void showCommunities(final MForm f) {
		savedItemId = null;
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
		f.removeAllCustomCommands();

		Command exit = new Command("Выйти") {
			public void actionPerformed(ActionEvent ev) {

				if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
					Display.getInstance().exitApplication();
				}

			}
		};

		f.setBackCommand(exit);

		Command addComm = new Command("Добавить", theme.getImage("1_add_new.png")) {
			public void actionPerformed(ActionEvent ev) {

				addJournalDialog("", "");
			}
		};
		f.addCustomCommand(addComm);

		Command friends = new Command("Друзья", theme.getImage("1_friends.png")) {
			public void actionPerformed(ActionEvent ev) {
				isTop = false;
				isFriends = true;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Лента", null);
				}

				communitiesForm.show();
				showFriends(communitiesForm);
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Топ", theme.getImage("1_top_users.png")) {
			public void actionPerformed(ActionEvent ev) {
				isTop = true;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
				}

				communitiesForm.show();
				showTop(communitiesForm);
			}
		};
		f.addCustomCommand(topUsers);

		Command users = new Command("Блогеры", theme.getImage("6_social_group.png")) {
			public void actionPerformed(ActionEvent ev) {
				isTop = false;
				isFriends = false;
				isUsers = true;
				isSaved = false;
				showUsers(f);
			}
		};
		f.addCustomCommand(users);

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
						longPressed = false;
					}

				};

				mb.setCommand(new Command("") {
					public void actionPerformed(ActionEvent ev) {
						if (!longPressed) {
							mb.setIcon(null);
							selectedID = "";

							lj_journal_name = uname;
							m_lj_journal_name = StringUtil.replaceAll(uname, "-", "_");
							selectedTag = "";
							clickedItem = null;
							clickedTagItem = null;
							isTags = false;
							selectedURL = "http://m.livejournal.com/read/user/" + m_lj_journal_name + "/";
							showTagsForm(f);
						}
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
		Storage.getInstance().writeObject(name, vec);
	}

	private Vector<String> loadList(String name) {
		@SuppressWarnings("unchecked")
		Vector<String> vec = (Vector<String>) Storage.getInstance().readObject(name);

		return vec;
	}

	private void showUsers(final MForm f) {
		savedItemId = null;
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
		f.removeAllCustomCommands();

		Command exit = new Command("Выйти") {
			public void actionPerformed(ActionEvent ev) {

				if (Dialog.show("Выход", "Выйти из приложения?", "     Да     ", "     Нет     ")) {
					Display.getInstance().exitApplication();
				}

			}
		};

		f.setBackCommand(exit);
		Command addComm = new Command("Добавить", theme.getImage("1_add_new.png")) {
			public void actionPerformed(ActionEvent ev) {

				addJournalDialog("", "");
			}
		};
		f.addCustomCommand(addComm);

		Command friends = new Command("Друзья", theme.getImage("1_friends.png")) {
			public void actionPerformed(ActionEvent ev) {
				isTop = false;
				isFriends = true;
				isUsers = false;
				isSaved = false;

				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
				}

				communitiesForm.show();
				showFriends(communitiesForm);
			}
		};
		f.addCustomCommand(friends);

		Command topUsers = new Command("Топ", theme.getImage("1_top_users.png")) {
			public void actionPerformed(ActionEvent ev) {

				isTop = true;
				isFriends = false;
				isUsers = false;
				isSaved = false;
				if (communitiesForm == null) {
					communitiesForm = newMyForm("Топ", null);
				}

				communitiesForm.show();
				showTop(communitiesForm);
			}
		};
		f.addCustomCommand(topUsers);

		Command communities = new Command("Сообщества", theme.getImage("6_social_person.png")) {
			public void actionPerformed(ActionEvent ev) {

				isTop = false;
				isFriends = false;
				isUsers = false;
				isSaved = false;

				showCommunities(f);
			}
		};

		f.addCustomCommand(communities);

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
						longPressed = false;

					}

				};

				mb.setCommand(new Command("") {
					public void actionPerformed(ActionEvent ev) {
						if (!longPressed) {
							mb.setIcon(null);
							selectedID = "";

							lj_journal_name = uname;
							m_lj_journal_name = StringUtil.replaceAll(uname, "-", "_");
							selectedTag = "";
							clickedItem = null;
							clickedTagItem = null;
							isTags = false;
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
		if (isShowHide) {
			Button ok = new Button(new Command("ОК") {
				public void actionPerformed(ActionEvent ev) {
					saveHiddenList(mainContainer, "HiddenUsers");
					isShowHide = false;
					showUsers(f);
				}
			});
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
		final String[] page = new String[1];
		int i = 0;
		String ext = null;
		while (pattern.match(html, i)) {
			String[] urlParts = Util.split(pattern.getParen(1), ".");
			if (urlParts.length > 0) {
				ext = urlParts[urlParts.length - 1];
				if (ext.length() > 3 || mimeMap.containsKey(ext.toUpperCase()))
					images.add(pattern.getParen(1));
			}
			i = pattern.getParenEnd(0);
		}
		final int numImages = images.size();
		page[0] = html;
		myLog("numImages " + numImages);

		Dialog dlg = new Dialog("Загрузка");
		dlg.setLayout(new BorderLayout());
		dlg.getContentPane().setUIID("DialogContentPane");
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
		dlg.getDialogComponent().addComponent(BorderLayout.SOUTH, bc);
		dlg.revalidate();
		dlg.showPacked(BorderLayout.CENTER, false);
		int progr = 0;
		final ImageIO imio = Display.getInstance().getImageIO();
		for (final String item : images) {
			slider.setText((int) Math.round((double) 100 * progr / (double) numImages) + "%");
			slider.setProgress(progr);

			if (isErrorShowed)
				break;
			MyConnectionRequest requestElement = new MyConnectionRequest() {

				EncodedImage img;

				@Override
				protected void postResponse() {

					if (img != null) {
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
						if (imgWidth > imgHeight) {

							if (imgWidth > 100) {
								newWidth = (int) Math.round(Math.sqrt(8000000 / numImages) / koef);
								if (newWidth > imgWidth)
									newWidth = imgWidth;

								if (newWidth < 100)
									newWidth = 100;
								else if (newWidth > 1000)
									newWidth = 1000;
								newHeight = (int) Math.round((double) newWidth / (double) imgWidth * imgHeight);
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
								newWidth = (int) Math.round((double) newHeight / (double) imgHeight * imgWidth);
							}

						}

						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ByteArrayInputStream is = new ByteArrayInputStream(img.getImageData());

						try {
							imio.save(is, os, ImageIO.FORMAT_JPEG, newWidth, newHeight, 0.7f);
							Util.cleanup(is);
							String encodedImg = WebBrowser.createDataURI(os.toByteArray(), mimeMap.get("JPG"));
							Util.cleanup(os);

							RE pattern = new RE("src=[\"']" + item + "[\"']", RE.MATCH_CASEINDEPENDENT);
							page[0] = pattern.subst(page[0], "src=\"" + encodedImg + "\"", RE.REPLACE_ALL);
						} catch (IOException e) {
							e.printStackTrace();
						}

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
			};

			requestElement.setUrl(item);
			requestElement.removeAllArguments();
			requestElement.setPost(false);
			isNetworkError = false;
			NetworkManager.getInstance().addToQueueAndWait(requestElement);
			progr++;
		}
		if (!isErrorShowed) {
			try {
				OutputStream out = Storage.getInstance().createOutputStream(pageURL);
				out.write(page[0].getBytes("UTF-8"));
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
		isSaving = false;
		if (dlg != null)
			dlg.dispose();

	}
}
