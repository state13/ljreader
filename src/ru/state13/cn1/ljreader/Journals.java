package ru.state13.cn1.ljreader;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Journals {

	static public LinkedHashMap<String, HashMap<String, String>> getLjCommunitiesList() {

		return new LinkedHashMap<String, HashMap<String, String>>() {
			{

				put("ru-travel", new HashMap<String, String>() {
					{
						put("name", "����������� � ������");
						put("tag_start", "<div class=\"j-w j-beta-w j-w-tags\">");
						put("tag_name", "<a href=\"http://ru-travel\\.livejournal\\.com/tag/.*?>(.*?)</a>");
						put("tag_num", "<a href=\"http://ru-travel\\.livejournal\\.com/tag/.*?title=\"([0-9]+).*?\">");

					}
				});

				put("kak-eto-sdelano", new HashMap<String, String>() {
					{
						put("name", "��� ��� �������");

					}
				});
				

				

				put("foturist-ru", new HashMap<String, String>() {
					{
						put("name", "Foturist");

					}
				});

				put("foto-history", new HashMap<String, String>() {
					{
						put("name", "������� � �����������");

					}
				});
				
				
				put("ru-psiholog", new HashMap<String, String>() {
					{
						put("name", "���� ���� ���������");
					}
				});

				put("ru-foto", new HashMap<String, String>() {
					{
						put("name", "������� ��������������");

					}
				});

				put("soviet-life", new HashMap<String, String>() {
					{
						put("name", "�������� ��������� �����");

					}
				});

				put("76-82", new HashMap<String, String>() {
					{
						put("name", "��� ���");
					}
				});
				
				put("ru-kino", new HashMap<String, String>() {
					{
						put("name", "���� - ������� ������������");

					}
				});

				put("drugoe-kino", new HashMap<String, String>() {
					{
						put("name", "������ ����");

					}
				});
				
				put("madeinrussia", new HashMap<String, String>() {
					{
						put("name", "Made in Russia");
					}
				});
				
				put("sdelano-u-nas", new HashMap<String, String>() {
					{
						put("name", "������� � ���");
					}
				});
				
				put("ru-auto", new HashMap<String, String>() {
					{
						put("name", "ru_auto");

					}
				});
				
				put("spb-auto", new HashMap<String, String>() {
					{
						put("name", "������������� ���������");

					}
				});
				
				put("moto-ru", new HashMap<String, String>() {
					{
						put("name", "���� �� ����������");

					}
				});

				put("ru-football", new HashMap<String, String>() {
					{
						put("name", "���������� ��������� �������");

					}
				});
				

				
				put("malyshi", new HashMap<String, String>() {
					{
						put("name", "������ - �� �������� �� ���� ���");

					}
				});
				



				
				put("ru-cats", new HashMap<String, String>() {
					{
						put("name", "Ru_Cats");

					}
				});
				
				put("ru-open", new HashMap<String, String>() {
					{
						put("name", "�������� ������");

					}
				});
				
				put("engineering-ru", new HashMap<String, String>() {
					{
						put("name", "���������");
//						put("tag_start", "<ul class=\"j-w-list j-w-list-tags j-p-tagcloud\">");
//						put("tag_name", "<li.*?><a href=\"http://engineering-ru\\.livejournal\\.com/tag/.*?>(.*?)</a>.*?</li>");
//						put("tag_num", "<a href=\"http://engineering-ru\\.livejournal\\.com/tag/.*?title=\"([0-9]+).*?\">");

					}
				});
				
				put("virtualshopping", new HashMap<String, String>() {
					{
						put("name", "���������� on-line");

					}
				});
				
				put("pora-valit", new HashMap<String, String>() {
					{
						put("name", "���� ������?");

					}
				});
				
				put("prophotos-ru", new HashMap<String, String>() {
					{
						put("name", "Prophotos");

					}
				});

				put("everyday-i-show", new HashMap<String, String>() {
					{
						put("name", "Everyday i show");

					}
				});

				put("your-look-today", new HashMap<String, String>() {
					{
						put("name", "��������, ��� ��������� ������");
					}
				});

				put("my-first-time", new HashMap<String, String>() {
					{
						put("name", "��� ������ ���");
//						put("item_start", "<article id=\"entry-my_first_time");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "<a href=\"http://my-first-time.livejournal.com/.*?\">(.*?)</a>");
//						put("item_date", "<span class=\"j-e-date-day\">(.*?)</span>");
//						put("item_id", "http://my-first-time.livejournal.com/([0-9]+)\\.html");
					}
				});


				put("moya-moskva", new HashMap<String, String>() {
					{
						put("name", "��� ������");

					}
				});

				put("stalic-kitchen", new HashMap<String, String>() {
					{
						put("name", "���������� ����������");

					}
				});

				put("foodclub-ru", new HashMap<String, String>() {
					{
						put("name", "Foodclub");

					}
				});

				put("ru-designer", new HashMap<String, String>() {
					{
						put("name", "������������ ����������");

					}
				});
				

				put("afisha-lj", new HashMap<String, String>() {
					{
						put("name", "�� �����������");

					}
				});
				
				put("master-klass", new HashMap<String, String>() {
					{
						put("name", "������-�����");

					}
				});

				put("art-expiration", new HashMap<String, String>() {
					{
						put("name", "���������� ����������� ������");

					}
				});
				
				put("ru-railway", new HashMap<String, String>() {
					{
						put("name", "�������� ������");

					}
				});

				put("ru-job", new HashMap<String, String>() {
					{
						put("name", "������");

					}
				});

				put("stars365", new HashMap<String, String>() {
					{
						put("name", "����� ������ ����!");

					}
				});

				put("ru-aviation", new HashMap<String, String>() {
					{
						put("name", "������ ����");

					}
				});
				
				put("interiors-ru", new HashMap<String, String>() {
					{
						put("name", "�������� ��� �� ����");
					}
				});

				put("kosmetichka", new HashMap<String, String>() {
					{
						put("name", "���������� - ��� � ��������� � ����������");

					}
				});
				
				put("make-your-style", new HashMap<String, String>() {
					{
						put("name", "������ ���� �����");

					}
				});
				
				

				put("ru-glamour", new HashMap<String, String>() {
					{
						put("name", "Fashion Victims");
					}
				});
				

				

				put("odin-moy-den", new HashMap<String, String>() {
					{
						put("name", "���� ��� ����");
					}
				});
				
				
				put("useful-faq", new HashMap<String, String>() {
					{
						put("name", "�������� �������");

					}
				});
				
				put("useless-faq", new HashMap<String, String>() {
					{
						put("name", "����������� �������");

					}
				});
				
				
				put("top", new HashMap<String, String>() {
					{
						put("name", "���");
						put("tag_start", "");
						put("tag_name", "");
						put("tag_num", "");
						put("item_start", "class=\"b-entryunit-entry\"");
						put("item_author", "<span class=\"lj-user\">(.*?)</span>");
						put("item_name", "<h3 class=\"b-entryunit-title\">(.*?)</h3>");
						put("item_date", "");
						put("item_id", "<a href=\"/read/user/.*?/([0-9]+)\">");
					}
				});
				
				put("friends", new HashMap<String, String>() {
					{
						put("name", "������");
						put("tag_start", "<body");
						put("tag_name", "\\.livejournal\\.com/tag/(.*?)[\"'/]");
						put("tag_num", "");
						put("item_start", "<li class=\"post-list-item\">");
						put("item_author", "<strong class=\"lj-user\">(.*?)</strong>");
						put("item_name", "<h3 class=\"item-header\">(.*?)</h3>");
						put("item_date", " (\\d{1,2}/\\d{2}/\\d{4}) ");
						put("item_id", "<a href=\"http://m.livejournal.com/read/user/.*?/([0-9]+)\">");
					}
				});

			}
		};
	}
	
	

//	static public LinkedHashMap<String, HashMap<String, String>> getLjCommunitiesListTag() {
//		return new LinkedHashMap<String, HashMap<String, String>>() {
//			{
//				put("76-82", new HashMap<String, String>() {
//					{
//						put("item_start", "<h2>");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "<a href=\"http://76-82.livejournal.com/.*?\">(.*?)</a>");
//						put("item_date", "<h2>(.*?)</h2>");
//						put("item_id", "http://76-82\\.livejournal\\.com/([0-9]+)\\.html");
//					}
//				});
//				
//				put("useless-faq", new HashMap<String, String>() {
//					{
//						put("item_start", "<h2>");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "<a href=\"http://useless-faq.livejournal.com/.*?\">(.*?)</a>");
//						put("item_date", "<h2>(.*?)</h2>");
//						put("item_id", "http://useless-faq\\.livejournal\\.com/([0-9]+)\\.html");
//					}
//				});
//				
//				put("useful-faq", new HashMap<String, String>() {
//					{
//						put("item_start", "<h2>");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "<a href=\"http://useful-faq.livejournal.com/.*?\">(.*?)</a>");
//						put("item_date", "<h2>(.*?)</h2>");
//						put("item_id", "http://useful-faq\\.livejournal\\.com/([0-9]+)\\.html");
//					}
//				});
//
//				put("moya-moskva", new HashMap<String, String>() {
//					{
//						put("item_start", "<h2>");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "<a href=\"http://moya-moskva.livejournal.com/.*?\">(.*?)</a>");
//						put("item_date", "<h2>(.*?)</h2>");
//						put("item_id", "http://moya-moskva\\.livejournal\\.com/([0-9]+)\\.html");
//					}
//				});
//
//				put("ru-designer", new HashMap<String, String>() {
//					{
//						put("item_start", "<h2>");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "<a href=\"http://ru-designer.livejournal.com/.*?\">(.*?)</a>");
//						put("item_date", "<h2>(.*?)</h2>");
//						put("item_id", "http://ru-designer\\.livejournal\\.com/([0-9]+)\\.html");
//					}
//				});
//
//			}
//		};
//	}

	static public LinkedHashMap<String, HashMap<String, String>> getLjUsersList() {

		return new LinkedHashMap<String, HashMap<String, String>>() {
			{

				put("sergeydolya", new HashMap<String, String>() {
					{
						put("name", "�������� ����������� ����������������");
						put("tag_start", "<dd class=\"tags\">");
						put("tag_name", "<li><a href=\"http://sergeydolya\\.livejournal\\.com/tag/.*?>(.*?)</a>.*?</li>");
						put("tag_num", "<li>.*?<span class=\"tag-count\">([0-9]+).*?</span></li>");
					}
				});
				
				put("pesen-net", new HashMap<String, String>() {
					{
						put("name", "����� ��.");
						
					}
				});
				
				put("fritzmorgen", new HashMap<String, String>() {
					{
						put("name", "����� ��� ���������");
						
					}
				});
				
				put("shpilenok", new HashMap<String, String>() {
					{
						put("name", "����� ��������");
						
					}
				});
				
				put("crimsonalter", new HashMap<String, String>() {
					{
						put("name", "crimsonalter");
						

					}
				});

				
				put("el-murid", new HashMap<String, String>() {
					{
						put("name", "��� �����");
						put("tag_start", "<table.*?class=\"tagstable\">");
						put("tag_name", "<td><a href=\"http://el-murid\\.livejournal\\.com/tag.*?>(.*?)</a></td>");
						put("tag_num", "<td><a href=\"http://el-murid\\.livejournal\\.com/tag.*?>.*?</a></td><td>([0-9]+)</td>");

					}
				});
				
				put("tema", new HashMap<String, String>() {
					{
						put("name", "������� �������");
						put("tag_start", "<ul class=\"ljtaglist\">");
						put("tag_name", "<li><a href=\"http://tema\\.livejournal\\.com/tag.*?>(.*?)</a>.*?</li>");
						put("tag_num", "<li><a href=\"http://tema\\.livejournal\\.com/tag.*?>.*?</a>.*?([0-9]+).*?</li>");
					}
				});
				
				put("bmpd", new HashMap<String, String>() {
					{
						put("name", "bmpd");
						
					}
				});

				
				put("chipstone", new HashMap<String, String>() {
					{
						put("name", "������ �� ����������");
						

					}
				});
				
				put("diak-kuraev", new HashMap<String, String>() {
					{
						put("name", "������ ������");
						
					}
				});
				
				put("varlamov.ru", new HashMap<String, String>() {
					{
						put("name", "varlamov.ru");
						put("tag_start", "<ul class=\"j-w-list j-w-list-tags j-p-tagcloud\">");
						put("tag_name", "<li.*?><a href=\"http://varlamov\\.ru/tag/.*?>(.*?)</a>.*?</li>");
						put("tag_num", "<a href=\"http://varlamov\\.ru/tag/.*?title=\"([0-9]+).*?\">");
					}
				});

				put("evo-lutio", new HashMap<String, String>() {
					{
						put("name", "��������");
						

					}
				});
				
				put("samsebeskazal", new HashMap<String, String>() {
					{
						put("name", "����� ������ �����������");
						
					}
				});

				
				put("colonelcassad", new HashMap<String, String>() {
					{
						put("name", "Colonel Cassad");
						

					}
				});
				
				put("putnik1", new HashMap<String, String>() {
					{
						put("name", "������ ��� �����");
						
					}
				});
				
				put("miss-tramell", new HashMap<String, String>() {
					{
						put("name", "���� ����");
						
					}
				});

				put("radulova", new HashMap<String, String>() {
					{
						put("name", "������� ��������");
						

					}
				});

				put("kukmor", new HashMap<String, String>() {
					{
						put("name", "������� ����");
						
					}
				});
				
				put("davydov-index", new HashMap<String, String>() {
					{
						put("name", "�������.������");
						put("tag_start", "<div class=\"content-tags\">");
						put("tag_name", "<a href=\"http://davydov-index\\.livejournal\\.com/tag.*?>(.*?)</a>");
						put("tag_num", "<a href=\"http://davydov-index\\.livejournal\\.com/tag.*?title=\"([0-9]+).*?\".*?>");
					}
				});
				
				
				put("nemihail", new HashMap<String, String>() {
					{
						put("name", "������� ���������");
						
					}
				});
				
				put("dpmmax", new HashMap<String, String>() {
					{
						put("name", "���� ������ ����������");
						

					}
				});
				
				put("masterok", new HashMap<String, String>() {
					{
						put("name", "��������.��.��");
						

					}
				});

				put("nikitskij", new HashMap<String, String>() {
					{
						put("name", "������ ���������");
						
					}
				});
				
				put("prostitutka-ket", new HashMap<String, String>() {
					{
						put("name", "��������� ����������");
						
					}
				});
				
				put("aquatek-filips", new HashMap<String, String>() {
					{
						put("name", "����� � ������������");
						

					}
				});
				
				put("alexcheban", new HashMap<String, String>() {
					{
						put("name", "���� � ������������");
						
					}
				});
				

				
				put("stalic", new HashMap<String, String>() {
					{
						put("name", "stalic");
						
					}
				});
				

				
				
				put("ibigdan", new HashMap<String, String>() {
					{
						put("name", "��� ������ � ��� ����");
						
					}
				});
				
				put("dr-piliulkin", new HashMap<String, String>() {
					{
						put("name", "������ ���������");
						put("tag_start", "<h2 class=\"asset-name page-header2\">Visible Tags</h2>");
						put("tag_name", "<li><a href=\"http://dr-piliulkin\\.livejournal\\.com/tag/.*?>(.*?)</a>.*?</li>");
						put("tag_num", "<a href=\"http://dr-piliulkin\\.livejournal\\.com/tag/.*?>.*?</a>.*?([0-9]+).*?</li>");
					}
				});
				

				
				put("kitya", new HashMap<String, String>() {
					{
						put("name", "Kitya Karlson");
						
//						put("item_start", "class=\"metabar\">");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "class=\"subj-link\">(.*?)</a>");
//						put("item_date", "<em>(.*?)</em>");
//						put("item_id", "http://tema.livejournal.com/(.*?)\\.html");
					}
				});
				
				
			
				
				put("cosharel", new HashMap<String, String>() {
					{
						put("name", "������� ������� � �������� � �����");
						
					}
				});
				

				

				
				put("exler", new HashMap<String, String>() {
					{
						put("name", "����� ������");
						
					}
				});

			
				put("dolboeb", new HashMap<String, String>() {
					{
						put("name", "����� ������� ������ ������");
						
					}
				});
				
				put("denokan", new HashMap<String, String>() {
					{
						put("name", "Fly Safe!");
						
					}
				});

				
				put("lovigin", new HashMap<String, String>() {
					{
						put("name", "ϸ�� �������");
						
					}
				});
				
				put("plakhov", new HashMap<String, String>() {
					{
						put("name", "�� ����������. �� �����������.");
						
					}
				});
			
				
				put("miumau", new HashMap<String, String>() {
					{
						put("name", "simply miu");
						
					}
				});
				

				put("sen-semilia", new HashMap<String, String>() {
					{
						put("name", "�������");
						put("tag_start", "<h2 class=\"asset-name page-header2\">Visible Tags</h2>");
						put("tag_name", "<li><a href=\"http://sen-semilia\\.livejournal\\.com/tag/.*?>(.*?)</a>.*?</li>");
						put("tag_num", "<a href=\"http://sen-semilia\\.livejournal\\.com/tag/.*?>.*?</a>.*?([0-9]+).*?</li>");
					}
				});
				

				
				put("kiss-my-abs", new HashMap<String, String>() {
					{
						put("name", "kiss my abs");
						

					}
				});
				

			}
		};
	}

//	static public LinkedHashMap<String, HashMap<String, String>> getLjUsersListTag() {
//
//		return new LinkedHashMap<String, HashMap<String, String>>();
//	}
}
