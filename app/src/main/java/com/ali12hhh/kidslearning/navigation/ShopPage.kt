package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import com.ali12hhh.kidslearning.R

private data class StoreItem(val id:String,val title:String,val category:String,val price:Int,val imageRes:Int)

private val storeItems=listOf(
 StoreItem("title_01","ملك النجوم","ألقاب النجوم",15,R.drawable.store_01_gold_crown),
 StoreItem("title_02","أمير المعرفة","ألقاب النجوم",20,R.drawable.store_02_silver_crown),
 StoreItem("title_03","نجم متألق","ألقاب النجوم",25,R.drawable.store_03_rainbow_star),
 StoreItem("title_04","بطل الماس","ألقاب النجوم",30,R.drawable.store_04_diamond_badge),
 StoreItem("title_05","نجم المستقبل","ألقاب النجوم",35,R.drawable.store_05_rocket),
 StoreItem("title_06","كأس التفوق","ألقاب النجوم",40,R.drawable.store_06_trophy),
 StoreItem("title_07","ساحر الكلمات","ألقاب المعرفة",45,R.drawable.store_07_magic_wand),
 StoreItem("title_08","حارس النجاح","ألقاب المعرفة",50,R.drawable.store_08_shield),
 StoreItem("title_09","فارس الإنجاز","ألقاب المعرفة",55,R.drawable.store_09_medal),
 StoreItem("title_10","بطل الشجاعة","ألقاب المعرفة",60,R.drawable.store_10_fire_badge),
 StoreItem("title_11","حالم النجوم","ألقاب المعرفة",20,R.drawable.store_11_moon_star),
 StoreItem("title_12","صانع النور","ألقاب المعرفة",25,R.drawable.store_12_sun_badge),
 StoreItem("title_13","قلب ذهبي","ألقاب الأبطال",30,R.drawable.store_13_heart_gem),
 StoreItem("title_14","قوس الفرح","ألقاب الأبطال",35,R.drawable.store_14_rainbow),
 StoreItem("title_15","كنز المعرفة","ألقاب الأبطال",40,R.drawable.store_15_book_gold),
 StoreItem("title_16","نجم المرح","ألقاب الأبطال",45,R.drawable.store_16_balloon),
 StoreItem("title_17","صديق الجميع","ألقاب الأبطال",50,R.drawable.store_17_gift),
 StoreItem("title_18","صانع الابتسامة","ألقاب الأبطال",55,R.drawable.store_18_sparkle),
 StoreItem("title_19","بطل الحروف","ألقاب التعلم",60,R.drawable.store_19_hero_badge),
 StoreItem("title_20","فارس الأرقام","ألقاب التعلم",65,R.drawable.store_20_genius_badge),
 StoreItem("title_21","المستكشف الصغير","ألقاب المغامرة",35,R.drawable.store_21_explorer),
 StoreItem("title_22","أسطورة صغيرة","ألقاب المغامرة",45,R.drawable.store_22_superstar),
 StoreItem("title_23","بطل الانطلاق","ألقاب المغامرة",55,R.drawable.store_23_little_hero),
 StoreItem("title_24","مستكشف الكواكب","ألقاب المغامرة",65,R.drawable.store_24_friend_star),
 StoreItem("title_25","قائد الإبداع","ألقاب الإبداع",70,R.drawable.store_25_creative),
 StoreItem("title_26","بطل التحدي","ألقاب الإبداع",75,R.drawable.store_26_brave_shield),
 StoreItem("title_27","قلب طيب","ألقاب الإبداع",80,R.drawable.store_27_kind_heart),
 StoreItem("title_28","سريع التعلم","ألقاب الإبداع",90,R.drawable.store_28_fast_learner),
 StoreItem("title_29","عقل لامع","ألقاب الأساطير",105,R.drawable.store_29_knowledge),
 StoreItem("title_30","أسطورة التعلم","ألقاب الأساطير",120,R.drawable.store_30_legend)
)

@Composable
fun ShopPage(initialCollection:Boolean=false,onBack:()->Unit){
 val context=LocalContext.current
 var collection by remember{mutableStateOf(initialCollection)}
 var category by remember{mutableStateOf("الكل")}
 var refresh by remember{mutableIntStateOf(0)}
 val stars=AppSettings.childStars(context)
 val owned=AppSettings.ownedItems(context)
 val cats=listOf("الكل","ألقاب النجوم","ألقاب المعرفة","ألقاب الأبطال","ألقاب التعلم","ألقاب المغامرة","ألقاب الإبداع","ألقاب الأساطير")
 val visible=if(collection) storeItems.filter{it.id in owned} else storeItems.filter{category=="الكل"||it.category==category}
 CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl){
  Column(Modifier.fillMaxSize().background(Color(0xFFF3F7FF)).padding(14.dp)){
   Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
    TextButton(onClick=onBack){Text("رجوع",fontWeight=FontWeight.Bold)}
    Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally){
     Text("متجر النجوم",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Black)
     Text("مكافآت تشجّع الطفل على التعلّم",fontSize=12.sp,color=Color(0xFF60708A))
    }
    Text("⭐ " + stars,fontWeight=FontWeight.Black,fontSize=18.sp)
   }
   Spacer(Modifier.height(8.dp))
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
    Button(onClick={collection=false},Modifier.weight(1f)){Text("المتجر")}
    Button(onClick={collection=true},Modifier.weight(1f)){Text("مقتنياتي (" + owned.size + ")")}
   }
   if(!collection){
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)){
     cats.forEach{c->TextButton(onClick={category=c}){Text(if(c==category)"● " + c else c,fontSize=12.sp,fontWeight=FontWeight.Bold)}}
    }
   }
   Spacer(Modifier.height(8.dp))
   if(visible.isEmpty()){
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Text("لا توجد مقتنيات بعد. اجمع النجوم من الدروس ثم عد إلى المتجر ⭐",textAlign=TextAlign.Center)}
   }else{
    LazyVerticalGrid(columns=GridCells.Fixed(2),modifier=Modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(12.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){
     items(visible,key={it.id}){item->
      val isOwned=item.id in owned
      Card(Modifier.fillMaxWidth().shadow(4.dp,RoundedCornerShape(22.dp)),shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(Color.White)){
       Column(Modifier.padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Image(painterResource(item.imageRes),item.title,Modifier.size(94.dp))
        Text(item.title,fontWeight=FontWeight.Black,textAlign=TextAlign.Center)
        Text(item.category,fontSize=10.sp,color=Color(0xFF6B7890),textAlign=TextAlign.Center)
        Spacer(Modifier.height(5.dp))
        when{
         isOwned->Text("✓ في مقتنياتك",color=Color(0xFF16803C),fontWeight=FontWeight.Black)
         stars>=item.price->Button(onClick={if(AppSettings.buyItem(context,item.id,item.price))refresh++},Modifier.fillMaxWidth()){Text("شراء ⭐ " + item.price,fontWeight=FontWeight.Black)}
         else->Button(onClick={},enabled=false,Modifier.fillMaxWidth()){Text("تحتاج ⭐ " + item.price)}
        }
       }
      }
     }
    }
   }
  }
 }
}
