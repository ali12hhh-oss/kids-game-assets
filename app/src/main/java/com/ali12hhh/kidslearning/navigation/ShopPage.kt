package com.ali12hhh.kidslearning.navigation

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
private data class StoreItem(val id:String,val title:String,val category:String,val price:Int,val art:Int)

private val storeItems=listOf(
 StoreItem("title_01","ملك النجوم","ألقاب النجوم",15,1),
 StoreItem("title_02","أمير المعرفة","ألقاب النجوم",20,2),
 StoreItem("title_03","نجم متألق","ألقاب النجوم",25,3),
 StoreItem("title_04","بطل الماس","ألقاب النجوم",30,4),
 StoreItem("title_05","نجم المستقبل","ألقاب النجوم",35,5),
 StoreItem("title_06","كأس التفوق","ألقاب النجوم",40,6),
 StoreItem("title_07","ساحر الكلمات","ألقاب المعرفة",45,7),
 StoreItem("title_08","حارس النجاح","ألقاب المعرفة",50,8),
 StoreItem("title_09","فارس الإنجاز","ألقاب المعرفة",55,9),
 StoreItem("title_10","بطل الشجاعة","ألقاب المعرفة",60,10),
 StoreItem("title_11","حالم النجوم","ألقاب المعرفة",20,11),
 StoreItem("title_12","صانع النور","ألقاب المعرفة",25,12),
 StoreItem("title_13","قلب ذهبي","ألقاب الأبطال",30,13),
 StoreItem("title_14","قوس الفرح","ألقاب الأبطال",35,14),
 StoreItem("title_15","كنز المعرفة","ألقاب الأبطال",40,15),
 StoreItem("title_16","نجم المرح","ألقاب الأبطال",45,16),
 StoreItem("title_17","صديق الجميع","ألقاب الأبطال",50,17),
 StoreItem("title_18","صانع الابتسامة","ألقاب الأبطال",55,18),
 StoreItem("title_19","بطل الحروف","ألقاب التعلم",60,19),
 StoreItem("title_20","فارس الأرقام","ألقاب التعلم",65,20),
 StoreItem("title_21","المستكشف الصغير","ألقاب المغامرة",35,21),
 StoreItem("title_22","أسطورة صغيرة","ألقاب المغامرة",45,22),
 StoreItem("title_23","بطل الانطلاق","ألقاب المغامرة",55,23),
 StoreItem("title_24","مستكشف الكواكب","ألقاب المغامرة",65,24),
 StoreItem("title_25","قائد الإبداع","ألقاب الإبداع",70,25),
 StoreItem("title_26","بطل التحدي","ألقاب الإبداع",75,26),
 StoreItem("title_27","قلب طيب","ألقاب الإبداع",80,27),
 StoreItem("title_28","سريع التعلم","ألقاب الإبداع",90,28),
 StoreItem("title_29","عقل لامع","ألقاب الأساطير",105,29),
 StoreItem("title_30","أسطورة التعلم","ألقاب الأساطير",120,30)
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
        StoreArtwork(item.art, item.title, Modifier.size(94.dp))
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

@Composable
private fun StoreArtwork(art: Int, description: String, modifier: Modifier = Modifier) {
    val palettes = listOf(
        Color(0xFFFFC94A), Color(0xFF8EC5FF), Color(0xFFFF8FA3),
        Color(0xFF9FE2BF), Color(0xFFB89CFF), Color(0xFFFFA66B),
        Color(0xFF65D8E8), Color(0xFFFFD86B), Color(0xFF7FC8FF), Color(0xFFFF9FCF)
    )
    val accent = palettes[(art - 1).mod(palettes.size)]
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawCircle(Color(0x1F20304A), radius = w * .43f, center = androidx.compose.ui.geometry.Offset(w*.52f,h*.56f))
        drawCircle(Color.White, radius = w * .42f, center = androidx.compose.ui.geometry.Offset(w*.5f,h*.48f))
        drawCircle(accent, radius = w * .34f, center = androidx.compose.ui.geometry.Offset(w*.5f,h*.48f))
        val center = androidx.compose.ui.geometry.Offset(w*.5f,h*.48f)
        when ((art - 1) % 6) {
            0 -> {
                val p = Path()
                for (i in 0 until 10) {
                    val angle = -Math.PI/2 + i*Math.PI/5
                    val r = if (i % 2 == 0) w*.25f else w*.11f
                    val x = center.x + kotlin.math.cos(angle).toFloat()*r
                    val y = center.y + kotlin.math.sin(angle).toFloat()*r
                    if (i == 0) p.moveTo(x,y) else p.lineTo(x,y)
                }
                p.close()
                drawPath(p, Color.White)
            }
            1 -> {
                drawRoundRect(Color.White, androidx.compose.ui.geometry.Offset(w*.30f,h*.25f),
                    androidx.compose.ui.geometry.Size(w*.40f,h*.42f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.06f))
                drawCircle(accent, radius=w*.08f, center=center)
            }
            2 -> {
                drawCircle(Color.White, radius=w*.18f, center=center)
                drawCircle(accent, radius=w*.10f, center=center)
                drawLine(Color.White, androidx.compose.ui.geometry.Offset(w*.24f,h*.72f), androidx.compose.ui.geometry.Offset(w*.76f,h*.72f), strokeWidth=w*.07f)
            }
            3 -> {
                drawRoundRect(Color.White, androidx.compose.ui.geometry.Offset(w*.27f,h*.31f),
                    androidx.compose.ui.geometry.Size(w*.46f,h*.34f), cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.12f))
                drawCircle(accent, radius=w*.09f, center=androidx.compose.ui.geometry.Offset(w*.5f,h*.48f))
                drawLine(Color.White, androidx.compose.ui.geometry.Offset(w*.50f,h*.18f), androidx.compose.ui.geometry.Offset(w*.50f,h*.31f), strokeWidth=w*.06f)
            }
            4 -> {
                drawCircle(Color.White, radius=w*.21f, center=center)
                drawLine(Color.White, androidx.compose.ui.geometry.Offset(w*.30f,h*.70f), androidx.compose.ui.geometry.Offset(w*.70f,h*.70f), strokeWidth=w*.08f)
                drawLine(Color.White, androidx.compose.ui.geometry.Offset(w*.38f,h*.78f), androidx.compose.ui.geometry.Offset(w*.62f,h*.78f), strokeWidth=w*.06f)
            }
            else -> {
                for (i in 0 until 4) {
                    val dx = if (i % 2 == 0) -.17f else .17f
                    val dy = if (i < 2) -.17f else .17f
                    drawCircle(Color.White, radius=w*.075f, center=androidx.compose.ui.geometry.Offset(w*(.5f+dx),h*(.48f+dy)))
                }
                drawCircle(accent, radius=w*.11f, center=center)
            }
        }
    }
}
