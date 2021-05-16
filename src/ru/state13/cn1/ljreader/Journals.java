package ru.state13.cn1.ljreader;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Journals {

	static public LinkedHashMap<String, HashMap<String, String>> getLjCommunitiesList() {

		return new LinkedHashMap<String, HashMap<String, String>>() {
			{

				put("ru-travel", new HashMap<String, String>() {
					{
						put("name", "Путешествия и прочее");
						put("tag_start", "<div class=\"j-w j-beta-w j-w-tags\">");
						put("tag_name", "<a href=\"http://ru-travel\\.livejournal\\.com/tag/.*?>(.*?)</a>");
						put("tag_num", "<a href=\"http://ru-travel\\.livejournal\\.com/tag/.*?title=\"([0-9]+).*?\">");

					}
				});

				put("kak-eto-sdelano", new HashMap<String, String>() {
					{
						put("name", "Как это сделано");

					}
				});
				

				

				put("foturist-ru", new HashMap<String, String>() {
					{
						put("name", "Foturist");

					}
				});

				put("foto-history", new HashMap<String, String>() {
					{
						put("name", "История в фотографиях");

					}
				});
				
				
				put("ru-psiholog", new HashMap<String, String>() {
					{
						put("name", "Сами себе психологи");
					}
				});

				put("ru-foto", new HashMap<String, String>() {
					{
						put("name", "Русское фотосообщество");

					}
				});

				put("soviet-life", new HashMap<String, String>() {
					{
						put("name", "Предметы советской жизни");

					}
				});

				put("76-82", new HashMap<String, String>() {
					{
						put("name", "Про нас");
					}
				});
				
				put("ru-kino", new HashMap<String, String>() {
					{
						put("name", "Кино - видимые удовольствия");

					}
				});

				put("drugoe-kino", new HashMap<String, String>() {
					{
						put("name", "Другое кино");

					}
				});
				
				put("madeinrussia", new HashMap<String, String>() {
					{
						put("name", "Made in Russia");
					}
				});
				
				put("sdelano-u-nas", new HashMap<String, String>() {
					{
						put("name", "Сделано у нас");
					}
				});
				
				put("ru-auto", new HashMap<String, String>() {
					{
						put("name", "ru_auto");

					}
				});
				
				put("spb-auto", new HashMap<String, String>() {
					{
						put("name", "Автомобильный Петербург");

					}
				});
				
				put("moto-ru", new HashMap<String, String>() {
					{
						put("name", "Люди на мотоциклах");

					}
				});

				put("ru-football", new HashMap<String, String>() {
					{
						put("name", "Сообщество любителей футбола");

					}
				});
				

				
				put("malyshi", new HashMap<String, String>() {
					{
						put("name", "Малыши - от рождения до семи лет");

					}
				});
				



				
				put("ru-cats", new HashMap<String, String>() {
					{
						put("name", "Ru_Cats");

					}
				});
				
				put("ru-open", new HashMap<String, String>() {
					{
						put("name", "Открытая Россия");

					}
				});
				
				put("engineering-ru", new HashMap<String, String>() {
					{
						put("name", "Инженерия");
//						put("tag_start", "<ul class=\"j-w-list j-w-list-tags j-p-tagcloud\">");
//						put("tag_name", "<li.*?><a href=\"http://engineering-ru\\.livejournal\\.com/tag/.*?>(.*?)</a>.*?</li>");
//						put("tag_num", "<a href=\"http://engineering-ru\\.livejournal\\.com/tag/.*?title=\"([0-9]+).*?\">");

					}
				});
				
				put("virtualshopping", new HashMap<String, String>() {
					{
						put("name", "Шопоголики on-line");

					}
				});
				
				put("pora-valit", new HashMap<String, String>() {
					{
						put("name", "Пора валить?");

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
						put("name", "Посмотри, как одеваются другие");
					}
				});

				put("my-first-time", new HashMap<String, String>() {
					{
						put("name", "Мой Первый Раз");
//						put("item_start", "<article id=\"entry-my_first_time");
//						put("item_author", "lj:user=\"(.*?)\"");
//						put("item_name", "<a href=\"http://my-first-time.livejournal.com/.*?\">(.*?)</a>");
//						put("item_date", "<span class=\"j-e-date-day\">(.*?)</span>");
//						put("item_id", "http://my-first-time.livejournal.com/([0-9]+)\\.html");
					}
				});


				put("moya-moskva", new HashMap<String, String>() {
					{
						put("name", "Моя Москва");

					}
				});

				put("stalic-kitchen", new HashMap<String, String>() {
					{
						put("name", "Кулинарное сообщество");

					}
				});

				put("foodclub-ru", new HashMap<String, String>() {
					{
						put("name", "Foodclub");

					}
				});

				put("ru-designer", new HashMap<String, String>() {
					{
						put("name", "Дизайнерское сообщество");

					}
				});
				

				put("afisha-lj", new HashMap<String, String>() {
					{
						put("name", "ЖЖ рекомендует");

					}
				});
				
				put("master-klass", new HashMap<String, String>() {
					{
						put("name", "Мастер-класс");

					}
				});

				put("art-expiration", new HashMap<String, String>() {
					{
						put("name", "Сообщество творческого выдоха");

					}
				});
				
				put("ru-railway", new HashMap<String, String>() {
					{
						put("name", "Железная дорога");

					}
				});

				put("ru-job", new HashMap<String, String>() {
					{
						put("name", "Руджоб");

					}
				});

				put("stars365", new HashMap<String, String>() {
					{
						put("name", "Рядом каждый день!");

					}
				});

				put("ru-aviation", new HashMap<String, String>() {
					{
						put("name", "Летное поле");

					}
				});
				
				put("interiors-ru", new HashMap<String, String>() {
					{
						put("name", "Интерьер как он есть");
					}
				});

				put("kosmetichka", new HashMap<String, String>() {
					{
						put("name", "Косметичка - все о косметике и парфюмерии");

					}
				});
				
				put("make-your-style", new HashMap<String, String>() {
					{
						put("name", "Создай свой стиль");

					}
				});
				
				

				put("ru-glamour", new HashMap<String, String>() {
					{
						put("name", "Fashion Victims");
					}
				});
				

				

				put("odin-moy-den", new HashMap<String, String>() {
					{
						put("name", "Один мой день");
					}
				});
				
				
				put("useful-faq", new HashMap<String, String>() {
					{
						put("name", "Полезные вопросы");

					}
				});
				
				put("useless-faq", new HashMap<String, String>() {
					{
						put("name", "Бесполезные вопросы");

					}
				});
				
				
				put("top", new HashMap<String, String>() {
					{
						put("name", "Топ");
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
						put("name", "Друзья");
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
						put("name", "Страница виртуальных путешественников");
						put("tag_start", "<dd class=\"tags\">");
						put("tag_name", "<li><a href=\"http://sergeydolya\\.livejournal\\.com/tag/.*?>(.*?)</a>.*?</li>");
						put("tag_num", "<li>.*?<span class=\"tag-count\">([0-9]+).*?</span></li>");
					}
				});
				
				put("pesen-net", new HashMap<String, String>() {
					{
						put("name", "Слава Сэ.");
						
					}
				});
				
				put("fritzmorgen", new HashMap<String, String>() {
					{
						put("name", "Место для дискуссий");
						
					}
				});
				
				put("shpilenok", new HashMap<String, String>() {
					{
						put("name", "Игорь Шпиленок");
						
					}
				});
				
				put("crimsonalter", new HashMap<String, String>() {
					{
						put("name", "crimsonalter");
						

					}
				});

				
				put("el-murid", new HashMap<String, String>() {
					{
						put("name", "Эль Мюрид");
						put("tag_start", "<table.*?class=\"tagstable\">");
						put("tag_name", "<td><a href=\"http://el-murid\\.livejournal\\.com/tag.*?>(.*?)</a></td>");
						put("tag_num", "<td><a href=\"http://el-murid\\.livejournal\\.com/tag.*?>.*?</a></td><td>([0-9]+)</td>");

					}
				});
				
				put("tema", new HashMap<String, String>() {
					{
						put("name", "Артемий Лебедев");
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
						put("name", "Взгляд на Зазеркалье");
						

					}
				});
				
				put("diak-kuraev", new HashMap<String, String>() {
					{
						put("name", "Андрей Кураев");
						
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
						put("name", "Эволюция");
						

					}
				});
				
				put("samsebeskazal", new HashMap<String, String>() {
					{
						put("name", "Будни одного бездельника");
						
					}
				});

				
				put("colonelcassad", new HashMap<String, String>() {
					{
						put("name", "Colonel Cassad");
						

					}
				});
				
				put("putnik1", new HashMap<String, String>() {
					{
						put("name", "Дорога без конца");
						
					}
				});
				
				put("miss-tramell", new HashMap<String, String>() {
					{
						put("name", "Лена Миро");
						
					}
				});

				put("radulova", new HashMap<String, String>() {
					{
						put("name", "Наталья Радулова");
						

					}
				});

				put("kukmor", new HashMap<String, String>() {
					{
						put("name", "Аксанов Нияз");
						
					}
				});
				
				put("davydov-index", new HashMap<String, String>() {
					{
						put("name", "Давыдов.Индекс");
						put("tag_start", "<div class=\"content-tags\">");
						put("tag_name", "<a href=\"http://davydov-index\\.livejournal\\.com/tag.*?>(.*?)</a>");
						put("tag_num", "<a href=\"http://davydov-index\\.livejournal\\.com/tag.*?title=\"([0-9]+).*?\".*?>");
					}
				});
				
				
				put("nemihail", new HashMap<String, String>() {
					{
						put("name", "Записки неМихаила");
						
					}
				});
				
				put("dpmmax", new HashMap<String, String>() {
					{
						put("name", "Блог добрых психиатров");
						

					}
				});
				
				put("masterok", new HashMap<String, String>() {
					{
						put("name", "Мастерок.жж.рф");
						

					}
				});

				put("nikitskij", new HashMap<String, String>() {
					{
						put("name", "Сергей Никитский");
						
					}
				});
				
				put("prostitutka-ket", new HashMap<String, String>() {
					{
						put("name", "Екатерина Безымянная");
						
					}
				});
				
				put("aquatek-filips", new HashMap<String, String>() {
					{
						put("name", "Жизнь в путешествиях");
						

					}
				});
				
				put("alexcheban", new HashMap<String, String>() {
					{
						put("name", "Ярко о путешествиях");
						
					}
				});
				

				
				put("stalic", new HashMap<String, String>() {
					{
						put("name", "stalic");
						
					}
				});
				

				
				
				put("ibigdan", new HashMap<String, String>() {
					{
						put("name", "Что попало в мои сети");
						
					}
				});
				
				put("dr-piliulkin", new HashMap<String, String>() {
					{
						put("name", "Доктор Пилюлькин");
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
						put("name", "Честные истории о мужчинах и жизни");
						
					}
				});
				

				

				
				put("exler", new HashMap<String, String>() {
					{
						put("name", "Алекс Экслер");
						
					}
				});

			
				put("dolboeb", new HashMap<String, String>() {
					{
						put("name", "Живые записки Антона Носика");
						
					}
				});
				
				put("denokan", new HashMap<String, String>() {
					{
						put("name", "Fly Safe!");
						
					}
				});

				
				put("lovigin", new HashMap<String, String>() {
					{
						put("name", "Пётр Ловыгин");
						
					}
				});
				
				put("plakhov", new HashMap<String, String>() {
					{
						put("name", "Не кинокритик. Не палеонтолог.");
						
					}
				});
			
				
				put("miumau", new HashMap<String, String>() {
					{
						put("name", "simply miu");
						
					}
				});
				

				put("sen-semilia", new HashMap<String, String>() {
					{
						put("name", "Татьяна");
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
