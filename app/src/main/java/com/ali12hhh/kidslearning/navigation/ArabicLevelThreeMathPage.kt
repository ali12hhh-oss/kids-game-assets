package com.ali12hhh.kidslearning.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.delay
import java.util.Locale

private data class ArithmeticExample(val left:Int,val right:Int,val answer:Int,val explanation:String)
private data class ArithmeticQuiz(val left:Int,val right:Int,val answer:Int,val options:List<Int>,val question:String)
private val addExamples=listOf(
ArithmeticExample(3,4,7,"ثلاثة زائد أربعة يساوي سبعة. نبدأ بثلاثة ثم نضيف أربعة: أربعة، خمسة، ستة، سبعة."),
ArithmeticExample(2,6,8,"اثنان زائد ستة يساوي ثمانية. نجمع الآحاد مع الآحاد فنحصل على ثمانية."),
ArithmeticExample(5,3,8,"خمسة زائد ثلاثة يساوي ثمانية. نضيف ثلاثة إلى خمسة: ستة، سبعة، ثمانية."),
ArithmeticExample(7,2,9,"سبعة زائد اثنين يساوي تسعة. نضيف الآحاد اثنين إلى سبعة."),
ArithmeticExample(4,5,9,"أربعة زائد خمسة يساوي تسعة. نجمع الرقمين في مرتبة الآحاد."),
ArithmeticExample(12,23,35,"اثنا عشر زائد ثلاثة وعشرون يساوي خمسة وثلاثين. الآحاد ٢ زائد ٣ يساوي ٥، والعشرات ١ زائد ٢ يساوي ٣."),
ArithmeticExample(24,15,39,"أربعة وعشرون زائد خمسة عشر يساوي تسعة وثلاثين. الآحاد ٤ زائد ٥ يساوي ٩، والعشرات ٢ زائد ١ يساوي ٣."),
ArithmeticExample(31,28,59,"واحد وثلاثون زائد ثمانية وعشرون يساوي تسعة وخمسين. الآحاد ١ زائد ٨ يساوي ٩، والعشرات ٣ زائد ٢ تساوي ٥."),
ArithmeticExample(46,22,68,"ستة وأربعون زائد اثنان وعشرون يساوي ثمانية وستين. الآحاد ٦ زائد ٢ يساوي ٨، والعشرات ٤ زائد ٢ تساوي ٦."),
ArithmeticExample(57,34,91,"سبعة وخمسون زائد أربعة وثلاثون يساوي واحدًا وتسعين. الآحاد ٧ زائد ٤ يساوي ١١، ثم نحتفظ بعشرة للعشرات."))
private val subExamples=listOf(
ArithmeticExample(8,3,5,"ثمانية ناقص ثلاثة يساوي خمسة. نبدأ بثمانية ونطرح ثلاثة فنصل إلى خمسة."),
ArithmeticExample(9,4,5,"تسعة ناقص أربعة يساوي خمسة. نحذف أربعة من تسعة، فيبقى خمسة."),
ArithmeticExample(7,2,5,"سبعة ناقص اثنين يساوي خمسة. نطرح الآحاد اثنين من سبعة."),
ArithmeticExample(6,1,5,"ستة ناقص واحد يساوي خمسة. عندما نطرح واحدًا من ستة يبقى خمسة."),
ArithmeticExample(5,2,3,"خمسة ناقص اثنين يساوي ثلاثة. نبدأ بخمسة ونزيل اثنين."),
ArithmeticExample(35,12,23,"خمسة وثلاثون ناقص اثنا عشر يساوي ثلاثة وعشرين. الآحاد ٥ ناقص ٢ يساوي ٣، والعشرات ٣ ناقص ١ يساوي ٢."),
ArithmeticExample(48,16,32,"ثمانية وأربعون ناقص ستة عشر يساوي اثنين وثلاثين. الآحاد ٨ ناقص ٦ يساوي ٢، والعشرات ٤ ناقص ١ تساوي ٣."),
ArithmeticExample(62,21,41,"اثنان وستون ناقص واحد وعشرون يساوي واحدًا وأربعين. الآحاد ٢ ناقص ١ يساوي ١، والعشرات ٦ ناقص ٢ تساوي ٤."),
ArithmeticExample(74,32,42,"أربعة وسبعون ناقص اثنان وثلاثون يساوي اثنين وأربعين. الآحاد ٤ ناقص ٢ يساوي ٢، والعشرات ٧ ناقص ٣ تساوي ٤."),
ArithmeticExample(83,27,56,"ثلاثة وثمانون ناقص سبعة وعشرين يساوي ستة وخمسين. نستلف عشرة من العشرات، فتصبح ١٣ ناقص ٧ يساوي ٦، ثم ٧ ناقص ٢ يساوي ٥."))
private val addQuiz=listOf(
ArithmeticQuiz(2,5,7,listOf(6,7,8),"ما ناتج ٢ + ٥؟"),ArithmeticQuiz(4,3,7,listOf(7,8,6),"ما ناتج ٤ + ٣؟"),
ArithmeticQuiz(6,2,8,listOf(9,7,8),"ما ناتج ٦ + ٢؟"),ArithmeticQuiz(1,8,9,listOf(8,9,10),"ما ناتج ١ + ٨؟"),
ArithmeticQuiz(5,4,9,listOf(7,8,9),"ما ناتج ٥ + ٤؟"),ArithmeticQuiz(12,14,26,listOf(24,26,28),"ما ناتج ١٢ + ١٤؟"),
ArithmeticQuiz(23,15,38,listOf(37,38,39),"ما ناتج ٢٣ + ١٥؟"),ArithmeticQuiz(34,25,59,listOf(58,59,60),"ما ناتج ٣٤ + ٢٥؟"),
ArithmeticQuiz(41,27,68,listOf(66,68,69),"ما ناتج ٤١ + ٢٧؟"),ArithmeticQuiz(56,33,89,listOf(87,89,90),"ما ناتج ٥٦ + ٣٣؟"))
private val subQuiz=listOf(
ArithmeticQuiz(8,3,5,listOf(4,5,6),"ما ناتج ٨ - ٣؟"),ArithmeticQuiz(9,2,7,listOf(6,7,8),"ما ناتج ٩ - ٢؟"),
ArithmeticQuiz(7,4,3,listOf(2,3,4),"ما ناتج ٧ - ٤؟"),ArithmeticQuiz(6,2,4,listOf(3,4,5),"ما ناتج ٦ - ٢؟"),
ArithmeticQuiz(5,1,4,listOf(3,4,5),"ما ناتج ٥ - ١؟"),ArithmeticQuiz(35,12,23,listOf(22,23,24),"ما ناتج ٣٥ - ١٢؟"),
ArithmeticQuiz(48,16,32,listOf(31,32,33),"ما ناتج ٤٨ - ١٦؟"),ArithmeticQuiz(62,21,41,listOf(40,41,42),"ما ناتج ٦٢ - ٢١؟"),
ArithmeticQuiz(74,32,42,listOf(41,42,43),"ما ناتج ٧٤ - ٣٢؟"),ArithmeticQuiz(83,27,56,listOf(55,56,57),"ما ناتج ٨٣ - ٢٧؟"))
private fun arDigits(v:Int)=v.toString().map{("٠".first().code+(it.code-"0".first().code)).toChar()}.joinToString("")

@Composable fun ArabicLevelThreeMathPage(onBack:()->Unit){
var operation by remember{mutableStateOf(0)}
Column(Modifier.fillMaxSize().background(Color(0xFFF4F7FF)).padding(14.dp)){
Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){TextButton(onClick=onBack){Text("‹ رجوع",fontWeight=FontWeight.ExtraBold)};Spacer(Modifier.weight(1f));Text("الرياضيات • المستوى الثالث",fontSize=21.sp,fontWeight=FontWeight.Black,color=Color(0xFF24324A))}
Spacer(Modifier.height(6.dp));TabRow(selectedTabIndex=operation){Tab(operation==0,{operation=0},text={Text("الجمع",fontWeight=FontWeight.Bold)});Tab(operation==1,{operation=1},text={Text("الطرح",fontWeight=FontWeight.Bold)})}
Spacer(Modifier.height(8.dp));AnimatedContent(targetState=operation,transitionSpec={fadeIn() togetherWith fadeOut()},label="operation"){if(it==0) OperationSection("الجمع",addExamples,addQuiz) else OperationSection("الطرح",subExamples,subQuiz)}}}

@Composable private fun OperationSection(title:String,examples:List<ArithmeticExample>,quizzes:List<ArithmeticQuiz>){var mode by remember(title){mutableStateOf(0)};Column(Modifier.fillMaxSize()){TabRow(selectedTabIndex=mode){Tab(mode==0,{mode=0},text={Text("تعلم",fontWeight=FontWeight.Bold)});Tab(mode==1,{mode=1},text={Text("اختبر نفسك",fontWeight=FontWeight.Bold)})};Spacer(Modifier.height(8.dp));if(mode==0)LearnOperation(title,examples)else QuizOperation(title,quizzes)}}

@Composable private fun LearnOperation(title:String,examples:List<ArithmeticExample>){
var index by remember(title){mutableStateOf(0)};val context=LocalContext.current;var ready by remember{mutableStateOf(false)};var tts by remember{mutableStateOf<TextToSpeech?>(null)};val ex=examples[index]
DisposableEffect(title){lateinit var e:TextToSpeech;e=TextToSpeech(context){s->if(s==TextToSpeech.SUCCESS){e.setLanguage(Locale.forLanguageTag("ar-XA"));e.setSpeechRate(0.82f);ready=true}};tts=e;onDispose{e.stop();e.shutdown()}}
Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)){Text("مثال "+arDigits(index+1)+" من "+arDigits(examples.size),fontWeight=FontWeight.Bold,color=Color(0xFF65738A));
Card(Modifier.fillMaxWidth().weight(1f).shadow(8.dp,RoundedCornerShape(26.dp)),shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(Color.White)){Column(Modifier.fillMaxSize().padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
Text(title,fontSize=22.sp,fontWeight=FontWeight.Black,color=if(title=="الجمع")Color(0xFF16A085)else Color(0xFFE05A5A));Spacer(Modifier.height(8.dp));
Text(arDigits(ex.left)+" "+(if(title=="الجمع")"+" else "-")+" "+arDigits(ex.right)+" = "+arDigits(ex.answer),fontSize=56.sp,fontWeight=FontWeight.Black,color=numberColor(index+1));
Text(if(index<5)"مثال من مرتبة واحدة" else "مثال من مرتبتين",fontSize=16.sp,fontWeight=FontWeight.Bold,color=Color(0xFF65738A));Spacer(Modifier.height(12.dp));
Text(ex.explanation,Modifier.fillMaxWidth(),fontSize=18.sp,lineHeight=28.sp,textAlign=TextAlign.Center,fontWeight=FontWeight.Bold);Spacer(Modifier.height(14.dp));
Button(onClick={if(ready)if (AppSettings.isSpeechEnabled(context)) tts?.speak(ex.explanation,TextToSpeech.QUEUE_FLUSH,null,"example_"+title+"_"+index)},Modifier.height(52.dp)){Text("🔊 اسمع الشرح",fontWeight=FontWeight.ExtraBold)}}}
Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Button(onClick={if(index>0)index--},Modifier.weight(1f).height(54.dp),colors=ButtonDefaults.buttonColors(Color(0xFF5B6B88))){Text("السابق",fontWeight=FontWeight.ExtraBold)};Button(onClick={if(index<examples.lastIndex)index++},Modifier.weight(1f).height(54.dp)){Text("التالي",fontWeight=FontWeight.ExtraBold)}}}}

@Composable private fun QuizOperation(title:String,quizzes:List<ArithmeticQuiz>){
var index by remember(title){mutableStateOf(0)};var selected by remember(title){mutableStateOf<Int?>(null)};var score by remember(title){mutableStateOf(0)};var reaction by remember(title){mutableStateOf(0)};val context=LocalContext.current;var ready by remember{mutableStateOf(false)};var tts by remember{mutableStateOf<TextToSpeech?>(null)};val q=quizzes[index]
DisposableEffect(title){lateinit var e:TextToSpeech;e=TextToSpeech(context){s->if(s==TextToSpeech.SUCCESS){e.setLanguage(Locale.forLanguageTag("ar-XA"));e.setSpeechRate(0.82f);ready=true}};tts=e;onDispose{e.stop();e.shutdown()}}
fun speak(text:String,id:String){if(ready)if (AppSettings.isSpeechEnabled(context)) tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,id)}
Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(6.dp)){
Text("سؤال "+arDigits(index+1)+" / "+arDigits(quizzes.size)+" • النتيجة "+arDigits(score),fontWeight=FontWeight.Bold,color=Color(0xFF65738A));
Card(Modifier.fillMaxWidth().shadow(7.dp,RoundedCornerShape(22.dp)),shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(Color.White)){Column(Modifier.fillMaxWidth().padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally){
Text(arDigits(q.left)+" "+(if(title=="الجمع")"+" else "-")+" "+arDigits(q.right)+" = ؟",fontSize=42.sp,fontWeight=FontWeight.Black,color=numberColor(index+1));Text(q.question,Modifier.fillMaxWidth(),fontSize=18.sp,fontWeight=FontWeight.Black,textAlign=TextAlign.Center);OutlinedButton(onClick={speak(q.question,"question_"+title+"_"+index)}){Text("🔊 صوت السؤال",fontWeight=FontWeight.ExtraBold)}}}
q.options.forEach{option->val correct=option==q.answer;val c=when{selected==option&&correct->Color(0xFF2EAD67);selected==option&&!correct->Color(0xFFE05A5A);else->Color.White};Button(onClick={if(selected==null){selected=option;if(correct){score++;reaction=1;AppSettings.awardCorrectAnswer(context);speak("أحسنت! إجابة صحيحة","correct_"+title+"_"+index)}else{reaction=-1;speak("حاول مرة ثانية","wrong_"+title+"_"+index)}}},Modifier.fillMaxWidth().height(48.dp),colors=ButtonDefaults.buttonColors(containerColor=c,contentColor=if(selected!=null)Color.White else Color(0xFF24324A))){Text(arDigits(option),fontSize=20.sp,fontWeight=FontWeight.Black)}}}
CharacterReactionMath(reaction)
Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Button(onClick={if(index>0){index--;selected=null;reaction=0}},Modifier.weight(1f),colors=ButtonDefaults.buttonColors(Color(0xFF5B6B88))){Text("السابق")};Button(onClick={if(index<quizzes.lastIndex){index++;selected=null;reaction=0}},Modifier.weight(1f)){Text("التالي")}}}

@Composable private fun CharacterReactionMath(reaction:Int){
val engine=rememberEngine();val loader=rememberModelLoader(engine);val model=remember{runCatching{loader.createModelInstance("Mannequin_Medium_Anim.glb")}.getOrNull()};val camera=rememberCameraNode(engine){position=Position(z=3.5f)};val node=remember(model){model?.let{ModelNode(modelInstance=it,autoAnimate=false,scaleToUnits=1.0f).also{it.position=Position(x=0f,y=-0.45f,z=0f)}}}
LaunchedEffect(reaction,node){val n=node?:return@LaunchedEffect;if(reaction==1){runCatching{n.stopAnimation(8)};runCatching{n.playAnimation(7,1f,false)};delay(1800);runCatching{n.stopAnimation(7)}}else if(reaction==-1){runCatching{n.stopAnimation(7)};runCatching{n.playAnimation(8,1f,false)}}else{runCatching{n.playAnimation(0,1f,true)}}}
Card(Modifier.fillMaxWidth().height(104.dp),shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(Color(0xFFF8FAFF))){Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Scene(modifier=Modifier.fillMaxSize(),engine=engine,modelLoader=loader,cameraNode=camera,cameraManipulator=null,isOpaque=false,childNodes=listOfNotNull(node))}}}

private fun numberColor(n:Int)=listOf(Color(0xFF315CFF),Color(0xFFE64A6B),Color(0xFF16A085),Color(0xFFE67E22),Color(0xFF7A4DCE),Color(0xFF008C95))[(n-1)%6]