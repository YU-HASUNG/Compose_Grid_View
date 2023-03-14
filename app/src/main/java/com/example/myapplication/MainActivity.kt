package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.TestDataModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.TestViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class MainActivity : ComponentActivity() {

    private val viewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()

        setContent {
            ListControllerView(
                viewModel
            )
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

@Composable
private fun ListControllerView(
    viewModel: TestViewModel
){

    val context = LocalContext.current
    val state = viewModel.state
    
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
        ){

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    viewModel.fetchData(0, 20)
                },
                modifier = Modifier
                    .fillMaxSize()
                ) {

                ListView(
                    list = viewModel.list,
                    onLoadMore = {

                        viewModel.fetchData(viewModel.list.size, 15)
                    }, onItemClick = {
                        Toast.makeText(context, "Index${it.index} item has clicked", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (state.loading.value) {

                Box(modifier = Modifier
                    .fillMaxSize()
                    .clickable { },
                contentAlignment = Alignment.Center
                ){

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(20.dp)
                    ){

                        CircularProgressIndicator(color = Color.White)

                    }
                }

            }
        }
    }
    
    LaunchedEffect(key1 = null, block = {
        viewModel.fetchData(0, 10)
    })
}

@Composable
private fun ListView(
    list: MutableList<TestDataModel>,
    onLoadMore: () -> Unit,
    onItemClick: (TestDataModel) -> Unit
){

    LazyVerticalGrid(
        columns = object: GridCells {
            override fun Density.calculateCrossAxisCellSizes(
                availableSize: Int,
                spacing: Int
            ): List<Int> {
                val firstColumn = (availableSize - spacing) * 2 / 3
                val secondColumn = (availableSize - spacing) - firstColumn

                return listOf(firstColumn, secondColumn)
            }

        }, contentPadding = PaddingValues(
            horizontal = 2.dp,
            vertical = 4.dp
        ),
        state = rememberLazyGridState(),
        modifier = Modifier.fillMaxSize()
    ){

        itemsIndexed(
            items = list,
            key = { _: Int, item: TestDataModel ->
                item.hashCode()
            },
            span = { _: Int, item: TestDataModel ->
                GridItemSpan(1)

            }
        ){ index, item ->

            ListViewItemView(
                model = item,
                onItemClick = onItemClick
            )

            if(index == list.size - 1) {
                
                LaunchedEffect(key1 = list.size, block = {
                    onLoadMore()
                })
            }
        }

    }

}

@Composable
private fun ListViewItemView(
    model: TestDataModel,
    onItemClick: (TestDataModel) -> Unit
){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 4.dp, vertical = 5.dp),
        shape = RoundedCornerShape(2.dp)
    ){

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.Black
                    ),
                    onClick = {
                        onItemClick(model)
                    }
                )
                .background(Color.White)
        ){

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            Image(
                painter = painterResource(id = model.imageResource),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            )

            Spacer(modifier = Modifier.padding(vertical = 6.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                text = "index: ${model.index}\n${model.content}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}