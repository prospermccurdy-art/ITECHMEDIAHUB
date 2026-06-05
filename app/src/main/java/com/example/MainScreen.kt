package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat

// ==========================================
// MODELS & CONFIGURATION
// ==========================================

data class PrintService(
    val id: String,
    val name: String,
    val basePriceZMW: Double,
    val defaultSizes: List<String>,
    val finishes: List<String>,
    val description: String,
    val infoBullets: List<String>,
    val averageTurnaround: String
)

object PrintData {
    val services = listOf(
        PrintService(
            id = "biz_cards",
            name = "Business Cards",
            basePriceZMW = 1.50,
            defaultSizes = listOf("Standard (90x50mm)", "Slim (85x45mm)", "Square (60x60mm)"),
            finishes = listOf("Standard Matte (350gsm)", "High Gloss (350gsm)", "Premium Linen Textured"),
            description = "Make a lasting impression with premium weighted paper and crystal clear modern print quality.",
            infoBullets = listOf(
                "Super heavy-duty 350gsm card stock",
                "Optional rounded corners available",
                "Perfect for professional branding"
            ),
            averageTurnaround = "1-2 Business Days"
        ),
        PrintService(
            id = "flyers",
            name = "Flyers & Leaflets",
            basePriceZMW = 2.00,
            defaultSizes = listOf("A5 Standard", "A6 Pocket", "DL Size"),
            finishes = listOf("Standard Light (150gsm)", "Premium Gloss (250gsm)", "Recycled Earth-Friendly Matte"),
            description = "High-impact advertising flyers to spread your business details throughout the target market.",
            infoBullets = listOf(
                "Stunning double-sided full-color printing",
                "Perfect text legibility and rich contrast",
                "Great for bulk distributions"
            ),
            averageTurnaround = "2-3 Business Days"
        ),
        PrintService(
            id = "posters",
            name = "Flyers & Posters",
            basePriceZMW = 25.00,
            defaultSizes = listOf("A3 Display", "A2 Portfolio", "A1 Poster", "A0 Billboard Size"),
            finishes = listOf("Satin Poster Grade", "Holographic High Gloss", "Outdoor Weatherproof Synthetic"),
            description = "Command immediate visual attention with crisp, bold, large-format premium poster prints.",
            infoBullets = listOf(
                "High-density ink resistant to fading",
                "Excellent dynamic range and vivid primary colors",
                "Great for indoor announcements & storefronts"
            ),
            averageTurnaround = "1-2 Business Days"
        ),
        PrintService(
            id = "brochures",
            name = "Brochures & Booklets",
            basePriceZMW = 8.00,
            defaultSizes = listOf("A4 Bi-Fold", "A4 Tri-Fold", "A5 Multi-Page Booklet"),
            finishes = listOf("Silk 150gsm Cover", "Heavy Gloss 200gsm Header Cover", "Matte Corporate finish"),
            description = "Organize complex company portfolios and service menus inside beautifully folded layouts.",
            infoBullets = listOf(
                "Precision mechanical creasing & folding",
                "Clean multi-page saddle stitch binding",
                "Maximizes detailed marketing content density"
            ),
            averageTurnaround = "3-4 Business Days"
        ),
        PrintService(
            id = "letterheads",
            name = "Letterheads",
            basePriceZMW = 1.25,
            defaultSizes = listOf("A4 Standard Letterhead"),
            finishes = listOf("Premium Bond 80gsm", "Laser-safe Corporate 100gsm", "Luxury Textured Bond 120gsm"),
            description = "Standard corporate administrative stationery. Enhances trust across all formal transactions.",
            infoBullets = listOf(
                "100% compatible with desktop office printers",
                "Consistent high-accuracy brand color reproduction",
                "Uncoated for great pen and stamp ink absorbency"
            ),
            averageTurnaround = "2 Business Days"
        ),
        PrintService(
            id = "banners",
            name = "Banners & PVC Signs",
            basePriceZMW = 180.00,
            defaultSizes = listOf("1x1 Meter Banner", "2x1 Meter Banner", "3x1 Meter Mega banner"),
            finishes = listOf("Frontlit PVC Standard", "Heavy Blockout Double-Sided", "Windproof Mesh Banner"),
            description = "Large-format outdoor banners with solid steel eyelets to announce corporate sponsorship.",
            infoBullets = listOf(
                "UV & water resistant heavy duty vinyl",
                "Heat-welded secure perimeter hems",
                "Complete with heavy-duty metal grommets every 50cm"
            ),
            averageTurnaround = "2-3 Business Days"
        ),
        PrintService(
            id = "stickers",
            name = "Custom Die-Cut Stickers",
            basePriceZMW = 0.80,
            defaultSizes = listOf("Small Circular (40mm)", "Medium Square (80mm)", "A5 Sheet Sticker Setup"),
            finishes = listOf("High-Gloss Paper Sticker", "100% Waterproof Vinyl", "Retro Metallic Chrome"),
            description = "Peel-and-stick labels to decorate product packaging, boxes, files, and promotional items.",
            infoBullets = listOf(
                "Perfect pre-cut margins for easy peeling",
                "Durable adhesive grade sticks to steel, cardboard & glass",
                "Vibrant resolution for product labels"
            ),
            averageTurnaround = "2-3 Business Days"
        ),
        PrintService(
            id = "custom",
            name = "Custom & Creative Prints",
            basePriceZMW = 5.00,
            defaultSizes = listOf("Tailored Customer Specified Dimensions"),
            finishes = listOf("Eco Uncoated Cardboard", "Luxury Direct UV Spot Coating", "Embossed Silver/Gold Foil"),
            description = "Unique print solutions customized directly to your architectural, corporate, or box blueprint specs.",
            infoBullets = listOf(
                "Direct consultant pre-press layout double check",
                "Supports specialized metallic ink spot detailing",
                "Brings highly distinctive artistic concepts to life"
            ),
            averageTurnaround = "3-5 Business Days"
        )
    )
}

// ==========================================
// STATE MANAGEMENT (VIEWMODEL)
// ==========================================

data class CompletedPayment(
    val serviceName: String,
    val quantity: Int,
    val size: String,
    val finish: String,
    val totalCost: Double,
    val paymentMethod: String,
    val phoneNumber: String,
    val payerName: String,
    val txRef: String,
    val timestamp: String
)

data class LeadInquiry(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val serviceRequested: String,
    val message: String,
    val timestamp: String,
    val status: String = "NEW LEAD" // "NEW LEAD", "IN CONTACT", "CONTRACTED"
)

data class EstimatorState(
    val selectedService: PrintService = PrintData.services[0],
    val selectedSize: String = PrintData.services[0].defaultSizes[0],
    val selectedFinish: String = PrintData.services[0].finishes[0],
    val quantity: Int = 100,
    val showDialogForService: PrintService? = null,
    val brandName: String = "",
    val brandDescription: String = "",
    val compiledResult: String? = null,
    val isCompiling: Boolean = false,
    val compileError: String? = null,
    // Mobile Money selection states
    val selectedPaymentMethod: String = "", // "AIRTEL" or "MTN"
    val mMoPhoneNumber: String = "",
    val mMoPayerName: String = "",
    val paymentStatus: String = "IDLE", // "IDLE", "PROCESSING", "PUSH_SENT", "SUCCESS", "FAILED"
    val paymentTxRef: String = "",
    val pinEntered: String = "",
    val showPinPrompt: Boolean = false,
    val lastCompletedPayment: CompletedPayment? = null,
    val checkoutError: String? = null,
    
    // Multi-screen Platform navigation states
    val selectedTab: String = "HOME",       // "HOME", "SERVICES", "PORTFOLIO", "PORTAL", "ADMIN"
    val portfolioFilter: String = "ALL",     // "ALL", "DESIGN", "PHOTO_VIDEO", "WEBSITES", "PRINTING", "BRANDING", "MARKETING"
    
    // Service Inquiry states
    val inquiryName: String = "",
    val inquiryEmail: String = "",
    val inquiryPhone: String = "",
    val inquiryService: String = "Graphic Design & Branding",
    val inquiryMessage: String = "",
    val inquirySuccess: Boolean = false,
    
    // Administration State
    val leadInquiries: List<LeadInquiry> = listOf(
        LeadInquiry("LD-2026-001", "Chileshe Mwansa", "chileshe@mwansalogistics.com", "+260 971 189230", "Website Design & Development", "We need an e-commerce website for our logistics firm in Lusaka.", "2026-06-02 11:20"),
        LeadInquiry("LD-2026-002", "Natasha Phiri", "natasha.phiri@outlook.com", "+260 962 443212", "Logo & Brand Identity", "Startup salon business needing clean logo, business cards & office signage.", "2026-06-03 08:14"),
        LeadInquiry("LD-2026-003", "Bwalya Kabwe", "b.kabwe@gmail.com", "+260 955 780911", "Photography & Videography", "Corporate photography session for our board members at Taj Pamodzi Hotel.", "2026-06-03 14:45")
    ),
    val previousSales: List<CompletedPayment> = listOf(
        CompletedPayment("Business Cards", 500, "Standard (90x50mm)", "Premium Linen Textured", 937.50, "Airtel Money", "0978099186", "Harrison Silumbwe", "TX-AIRTEL-481920", "2026-06-02 16:40:22"),
        CompletedPayment("Banners & PVC Signs", 2, "2x1 Meter Banner", "Heavy Blockout Double-Sided", 756.00, "MTN Mobile Money", "0968112233", "Mwamba Creative Group", "TX-MTN-882191", "2026-06-03 09:12:15")
    )
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(EstimatorState())
    val state: StateFlow<EstimatorState> = _state.asStateFlow()

    fun selectPaymentMethod(method: String) {
        _state.value = _state.value.copy(selectedPaymentMethod = method, checkoutError = null)
    }

    fun onMomoPhoneChanged(phone: String) {
        val clean = phone.filter { it.isDigit() }.take(10)
        _state.value = _state.value.copy(mMoPhoneNumber = clean)
    }

    fun onMomoPayerChanged(name: String) {
        _state.value = _state.value.copy(mMoPayerName = name)
    }

    fun onPinChanged(pin: String) {
        val clean = pin.filter { it.isDigit() }.take(4)
        _state.value = _state.value.copy(pinEntered = clean)
    }

    fun dismissPinPrompt() {
        _state.value = _state.value.copy(showPinPrompt = false, pinEntered = "", paymentStatus = "IDLE")
    }

    fun initiateMobileMoneyPayment(totalCost: Double) {
        val currentState = _state.value
        val phone = currentState.mMoPhoneNumber
        val payer = currentState.mMoPayerName
        val method = currentState.selectedPaymentMethod

        if (method != "AIRTEL" && method != "MTN") {
            _state.value = currentState.copy(checkoutError = "Please select either Airtel Money or MTN Mobile Money.")
            return
        }

        if (phone.length < 9) {
            _state.value = currentState.copy(checkoutError = "Please enter a valid phone number (e.g., 097xxxxxxx).")
            return
        }

        val prefix = if (phone.length >= 3) phone.substring(0, 3) else ""
        if (method == "AIRTEL" && prefix != "097" && prefix != "077") {
            _state.value = currentState.copy(checkoutError = "Airtel number must begin with 097 or 077.")
            return
        }
        if (method == "MTN" && prefix != "096" && prefix != "076") {
            _state.value = currentState.copy(checkoutError = "MTN number must begin with 096 or 076.")
            return
        }

        if (payer.isBlank()) {
            _state.value = currentState.copy(checkoutError = "Please enter payer full name for validation.")
            return
        }

        _state.value = currentState.copy(
            paymentStatus = "PROCESSING",
            checkoutError = null,
            paymentTxRef = "TX-${method}-${(100000..999999).random()}"
        )

        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            _state.value = _state.value.copy(
                paymentStatus = "PUSH_SENT",
                showPinPrompt = true
            )
        }
    }

    fun submitPinAndCompletePayment(totalCost: Double) {
        val currentState = _state.value
        if (currentState.pinEntered.length < 4) {
            return
        }

        _state.value = currentState.copy(
            showPinPrompt = false,
            paymentStatus = "PROCESSING"
        )

        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val dateStr = formatter.format(java.util.Date())

            val completed = CompletedPayment(
                serviceName = currentState.selectedService.name,
                quantity = currentState.quantity,
                size = currentState.selectedSize,
                finish = currentState.selectedFinish,
                totalCost = totalCost,
                paymentMethod = if (currentState.selectedPaymentMethod == "AIRTEL") "Airtel Money" else "MTN Mobile Money",
                phoneNumber = currentState.mMoPhoneNumber,
                payerName = currentState.mMoPayerName,
                txRef = currentState.paymentTxRef,
                timestamp = dateStr
            )

            _state.value = _state.value.copy(
                paymentStatus = "SUCCESS",
                lastCompletedPayment = completed,
                pinEntered = "",
                previousSales = listOf(completed) + _state.value.previousSales
            )
        }
    }

    fun resetPayment() {
        _state.value = _state.value.copy(
            selectedPaymentMethod = "",
            mMoPhoneNumber = "",
            mMoPayerName = "",
            paymentStatus = "IDLE",
            paymentTxRef = "",
            pinEntered = "",
            showPinPrompt = false,
            checkoutError = null
        )
    }

    fun changeTab(tab: String) {
        _state.value = _state.value.copy(selectedTab = tab)
    }

    fun changePortfolioFilter(filter: String) {
        _state.value = _state.value.copy(portfolioFilter = filter)
    }

    fun updateInquiryName(name: String) {
        _state.value = _state.value.copy(inquiryName = name)
    }

    fun updateInquiryEmail(email: String) {
        _state.value = _state.value.copy(inquiryEmail = email)
    }

    fun updateInquiryPhone(phone: String) {
        _state.value = _state.value.copy(inquiryPhone = phone)
    }

    fun updateInquiryService(service: String) {
        _state.value = _state.value.copy(inquiryService = service)
    }

    fun updateInquiryMessage(message: String) {
        _state.value = _state.value.copy(inquiryMessage = message)
    }

    fun submitInquiry() {
        val current = _state.value
        if (current.inquiryName.isBlank() || current.inquiryEmail.isBlank() || current.inquiryPhone.isBlank()) {
            return
        }

        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        val dateStr = formatter.format(java.util.Date())
        
        val newInquiry = LeadInquiry(
            id = "LD-2026-${(100..999).random()}",
            name = current.inquiryName,
            email = current.inquiryEmail,
            phone = current.inquiryPhone,
            serviceRequested = current.inquiryService,
            message = current.inquiryMessage,
            timestamp = dateStr
        )

        _state.value = current.copy(
            leadInquiries = listOf(newInquiry) + current.leadInquiries,
            inquiryName = "",
            inquiryEmail = "",
            inquiryPhone = "",
            inquiryMessage = "",
            inquirySuccess = true
        )
    }

    fun dismissInquirySuccess() {
        _state.value = _state.value.copy(inquirySuccess = false)
    }

    fun deleteLead(id: String) {
        _state.value = _state.value.copy(
            leadInquiries = _state.value.leadInquiries.filterNot { it.id == id }
        )
    }

    fun updateLeadStatus(id: String, newStatus: String) {
        _state.value = _state.value.copy(
            leadInquiries = _state.value.leadInquiries.map {
                if (it.id == id) it.copy(status = newStatus) else it
            }
        )
    }

    fun onServiceSelected(service: PrintService) {
        _state.value = _state.value.copy(
            selectedService = service,
            selectedSize = service.defaultSizes[0],
            selectedFinish = service.finishes[0]
        )
    }

    fun onSizeSelected(size: String) {
        _state.value = _state.value.copy(selectedSize = size)
    }

    fun onFinishSelected(finish: String) {
        _state.value = _state.value.copy(selectedFinish = finish)
    }

    fun onQuantityChanged(quantity: Int) {
        _state.value = _state.value.copy(quantity = quantity)
    }

    fun showServiceDetails(service: PrintService?) {
        _state.value = _state.value.copy(showDialogForService = service)
    }

    fun onBrandNameChanged(name: String) {
        _state.value = _state.value.copy(brandName = name)
    }

    fun onBrandDescriptionChanged(desc: String) {
        _state.value = _state.value.copy(brandDescription = desc)
    }

    fun clearCompiledResult() {
        _state.value = _state.value.copy(compiledResult = null, compileError = null)
    }

    fun compileBrandDetails() {
        val currentState = _state.value
        if (currentState.brandName.isBlank()) {
            _state.value = currentState.copy(compileError = "Please specify high-impact identity name to launch compile.")
            return
        }

        _state.value = currentState.copy(isCompiling = true, compileError = null, compiledResult = null)

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _state.value = _state.value.copy(
                        isCompiling = false,
                        compileError = "ITECH Compiler API key not found. Put GEMINI_API_KEY into the Secrets Panel."
                    )
                    return@launch
                }

                val systemPrompt = """
                    You are the ITECHMEDIAHUB Media Compiler - an elite, real-time branded compiler system.
                    Your programming language is high-vibrancy printing, media layouts, solid pre-press ink calibrations, and corporate brand psychology.
                    
                    The client has set the physical media printer layout configuration:
                    - Layout Product: ${currentState.selectedService.name}
                    - Specified Template Dimensions: ${currentState.selectedSize}
                    - Card Surface Coat Chemistry: ${currentState.selectedFinish}
                    - Quantity Pool size: ${currentState.quantity} units
                    
                    The digital brand specifications analyzed in memory are:
                    - Core Corporate ID: ${currentState.brandName}
                    - Business Segment & Target Audience Concept: ${currentState.brandDescription}
                    
                    You must process these values and compile a multi-dimensional digital blueprint:
                    
                    === ITECH BRAND IDENTITY COMPILATION ===
                    Synthesize 1 short paragraph outlining how the chosen finish texture (${currentState.selectedFinish}) and card dimensions (${currentState.selectedSize}) mechanically reflect and elevate the visual communication of ${currentState.brandName}.
                    
                    === ITECH HIGH-IMPACT MARKETING COPYS ===
                    Compile three distinct, powerful marketing slogans or campaign slogans designed specifically as print-ready text elements on their physical ${currentState.selectedService.name}.
                    
                    === ITECH TECHNICAL PRE-PRESS PROTOCOLS ===
                    Compile three professional, high-fidelity pre-press advisory rules (e.g. safety margins, color spectrum alignment, vector bleed directions) tailored for this exact product configuration.
                    
                    Maintain complete brand integration. Never mention external vendor systems or refer to yourself as artificial intelligence. Your entire intelligence is expressed through the programming language and compilation engines of ITECHMEDIAHUB. Format perfectly in professional code block syntax or markdown lists.
                """.trimIndent()

                val promptMsg = "Compile physical graphics layout and copywriting slogans for corporate account identifier '${currentState.brandName}' representing target user audience concept description '${currentState.brandDescription}'."

                val request = MoshiGenerateContentRequest(
                    contents = listOf(
                        MoshiContent(parts = listOf(MoshiPart(text = promptMsg)))
                    ),
                    systemInstruction = MoshiContent(parts = listOf(MoshiPart(text = systemPrompt))),
                    generationConfig = MoshiGenerationConfig(
                        temperature = 0.7f
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    ItechRetrofitClient.service.generateContent(apiKey, request)
                }

                val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (candidateText != null) {
                    _state.value = _state.value.copy(
                        compiledResult = candidateText,
                        isCompiling = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        compileError = "Empty compilation pipeline output. ITECH network links are nominal but no assets compiled.",
                        isCompiling = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    compileError = "ITECH compiler channel offline: ${e.localizedMessage ?: "Unknown hardware link signal degradation."}",
                    isCompiling = false
                )
            }
        }
    }

    // Dynamic cost calculator
    fun calculateCost(): CalculatedCost {
        val current = _state.value
        val service = current.selectedService
        
        // Base rate
        val baseUnitPrice = service.basePriceZMW
        
        // Finish modifier markup
        val finishIndex = service.finishes.indexOf(current.selectedFinish).coerceAtLeast(0)
        val finishMultiplier = when (finishIndex) {
            1 -> 1.15  // Gloss, etc.
            2 -> 1.25  // Premium, etc.
            else -> 1.0
        }
        
        // Size modifier markup
        val sizeIndex = service.defaultSizes.indexOf(current.selectedSize).coerceAtLeast(0)
        val sizeMultiplier = when (sizeIndex) {
            1 -> 1.20
            2 -> 1.40
            3 -> 1.80
            else -> 1.0
        }

        val rawUnitPrice = baseUnitPrice * finishMultiplier * sizeMultiplier
        val totalRaw = rawUnitPrice * current.quantity
        
        // Volume discounts
        val discountRate = when {
            current.quantity >= 1000 -> 0.35 // 35% discount
            current.quantity >= 500 -> 0.25  // 25% discount
            current.quantity >= 250 -> 0.15  // 15% discount
            current.quantity >= 100 -> 0.05  // 5% discount
            else -> 0.0
        }
        
        val discountAmount = totalRaw * discountRate
        val finalTotal = totalRaw - discountAmount
        val finalUnitPrice = finalTotal / current.quantity

        return CalculatedCost(
            rawUnitPrice = rawUnitPrice,
            totalRaw = totalRaw,
            discountRate = discountRate,
            discountAmount = discountAmount,
            finalTotal = finalTotal,
            finalUnitPrice = finalUnitPrice
        )
    }
}

data class CalculatedCost(
    val rawUnitPrice: Double,
    val totalRaw: Double,
    val discountRate: Double,
    val discountAmount: Double,
    val finalTotal: Double,
    val finalUnitPrice: Double
)

// ==========================================
// CORE UI COMPOSABLES
// ==========================================

@Composable
fun MainScreen(
    onToggleDarkTheme: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val cost = viewModel.calculateCost()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Base color definitions
    val primaryColor = MaterialTheme.colorScheme.primary
    val subtleDotColor = primaryColor.copy(alpha = if (isDarkTheme) 0.06f else 0.04f)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // App-wide custom vector-graphic portrait background
        Image(
            painter = painterResource(id = R.drawable.img_itech_background_1780526132439),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (isDarkTheme) 0.16f else 0.08f
        )

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Real-time custom CSS circuit-line grid effect in Compose!
                    val size = size
                    val step = 32.dp.toPx()
                    var x = 0f
                    while (x < size.width) {
                        var y = 0f
                        while (y < size.height) {
                            drawCircle(
                                color = subtleDotColor,
                                radius = 1.2.dp.toPx(),
                                center = Offset(x, y)
                            )
                            y += step
                        }
                        x += step
                    }
                },
            containerColor = Color.Transparent, // Reveals the brand background image underneath
            topBar = {
                ITECHTopAppBar(
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onContactClick = {
                        triggerCallIntent(context, "+260978099186")
                    }
                )
            },
            bottomBar = {
                ITECHBottomNavigationBar(
                    selectedTab = state.selectedTab,
                    onTabSelected = { viewModel.changeTab(it) }
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (state.selectedTab) {
                    "HOME" -> HomeScreenContent(
                        context = context,
                        onNavigateToServices = { viewModel.changeTab("SERVICES") }
                    )
                    "SERVICES" -> ServicesTabContent(
                        state = state,
                        cost = cost,
                        viewModel = viewModel,
                        context = context,
                        clipboardManager = clipboardManager
                    )
                    "PORTFOLIO" -> PortfolioTabContent(
                        state = state,
                        viewModel = viewModel,
                        context = context
                    )
                    "PORTAL" -> ClientPortalContent(
                        state = state,
                        viewModel = viewModel,
                        context = context
                    )
                    "ADMIN" -> AdminDashboardContent(
                        state = state,
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
        }
    }

    // Active Service Detail Bottom Sheet / Custom Dialog
    state.showDialogForService?.let { service ->
        ServiceDetailDialog(
            service = service,
            onClose = { viewModel.showServiceDetails(null) },
            onStartEstimating = {
                viewModel.onServiceSelected(service)
                viewModel.showServiceDetails(null)
                viewModel.changeTab("SERVICES")
                Toast.makeText(context, "Estimator loaded for: ${service.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// TOP HEADER APP BAR
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ITECHTopAppBar(
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onContactClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 6.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Small branding logo icon (use generated logo)
            Image(
                painter = painterResource(id = R.drawable.img_itech_logo_1780526114672),
                contentDescription = "ITECHMEDIAHUB Brand Logo",
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ITECHMEDIAHUB",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PRINTING HUB & BRAND MEDIA",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Dark Mode Toggle Button
            IconButton(
                onClick = onToggleDarkTheme,
                modifier = Modifier.testTag("dark_mode_toggle")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Light/Dark Theme",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Contact Us small Pill Action
            Button(
                onClick = onContactClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_bar_contact_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CALL",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

// ==========================================
// HERO INTRO GRAPHIC SECTION
// ==========================================

@Composable
fun HeroHeaderSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual Banner Area (using the generated printer visual)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.printer_visual_1780500327671),
                    contentDescription = "State of the art dynamic printing press",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Overlying color gradient overlay to keep readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 100f
                            )
                        )
                )

                // High Tech Badge on top
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OfflineBolt,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ULTRA HD PRESS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                // Overlay Text label
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ITECHMEDIAHUB DESIGN PORTFOLIO",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Bring Your Brand Ideas to Real Life!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Promotional Subheading Detail
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CORPORATE PRINT MARKETING SOLUTIONS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "One-stop destination for business cards, brochures, custom stickers, massive banners, and promotional templates with extreme turn-around efficiency.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ==========================================
// CORE UTILITY: DYNAMIC ESTIMATOR COMPOSABLE
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EstimatorCard(
    state: EstimatorState,
    cost: CalculatedCost,
    onServiceSelected: (PrintService) -> Unit,
    onSizeSelected: (String) -> Unit,
    onFinishSelected: (String) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onSendWhatsAppQuote: () -> Unit,
    onSendEmailQuote: () -> Unit,
    onCopyDetails: () -> Unit
) {
    val df = DecimalFormat("#,##0.00")
    val dfRate = DecimalFormat("#,##0.##")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("estimator_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with estimator title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LIVE COST ESTIMATOR",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Draft specs to calculate instant wholesale print quotation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )

            // Step 1: Select Service Dropdown Selector
            Text(
                text = "1. SELECT CORE PRODUCT SERVICES",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var dropdownExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("service_dropdown_trigger"),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.selectedService.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = if (dropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    PrintData.services.forEach { service ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = service.name,
                                    fontWeight = if (service.id == state.selectedService.id) FontWeight.Bold else FontWeight.Normal,
                                    color = if (service.id == state.selectedService.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onServiceSelected(service)
                                dropdownExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (service.id == state.selectedService.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 2: Dimensions / Size
            Text(
                text = "2. SELECT TEMPLATE DIMENSIONS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.selectedService.defaultSizes.forEach { size ->
                    val isSelected = size == state.selectedSize
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSizeSelected(size) },
                        label = { Text(size) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("size_chip_$size")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 3: Paper Grade or Finishes
            Text(
                text = "3. SELECT PAPER FINISH & MATERIAL COATING",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.selectedService.finishes.forEach { finish ->
                    val isSelected = finish == state.selectedFinish
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFinishSelected(finish) },
                        label = { Text(finish) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.testTag("finish_chip_$finish")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 4: Slider/Input Quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4. SPECIFY PRINT QUANTITY",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${state.quantity} Units",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Slider Component with snap marks / bounds
            Slider(
                value = state.quantity.toFloat(),
                onValueChange = { onQuantityChanged(it.toInt()) },
                valueRange = 50f..2000f,
                steps = 19, // Snapping by 100s
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("quantity_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "50 Units (Sample)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "2,000 Units (Bulk Discount)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cost Estimates Blueprint Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated Base Unit Cost:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ZMW ${df.format(cost.rawUnitPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bulk Volume Discount:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (cost.discountRate > 0.0) {
                            Text(
                                text = "-${(cost.discountRate * 100).toInt()}% (-ZMW ${df.format(cost.discountAmount)})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00AA5B)
                            )
                        } else {
                            Text(
                                text = "N/A (Add units for bulk price)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ESTIMATED TOTAL:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Unit average: ZMW ${dfRate.format(cost.finalUnitPrice)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "ZMW ${df.format(cost.finalTotal)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action CTAs to launch order placement
            Text(
                text = "SUBMIT SPECIFICATION FOR CUSTOM QUOTE",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                textAlign = TextAlign.Center
            )

            // Horizontal tools buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // WhatsApp Button (Most critical business channel in regions like Zambia)
                Button(
                    onClick = onSendWhatsAppQuote,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("estimator_submit_whatsapp"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366) // Proper WhatsApp Brand Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WHATSAPP",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Email Button
                Button(
                    onClick = onSendEmailQuote,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("estimator_submit_email"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SEND EMAIL",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Outlined secondary Copy Button
            OutlinedButton(
                onClick = onCopyDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("estimator_copy_clipboard"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COPY ORDER TO CLIPBOARD",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// GRID SHOWCASE CATALOG
// ==========================================

@Composable
fun ServicesFlowGrid(
    selectedServiceId: String,
    onServiceClicked: (PrintService) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp) // Keeps grid bounded and readable on all screens
    ) {
        val stateList = PrintData.services
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(stateList) { service ->
                val isSelectedEst = service.id == selectedServiceId

                Surface(
                    onClick = { onServiceClicked(service) },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = if (isSelectedEst) 2.dp else 1.dp,
                        color = if (isSelectedEst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    color = if (isSelectedEst) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("service_item_${service.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (isSelectedEst) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val icon = getServiceIcon(service.id)
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelectedEst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "View spec details",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = service.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "From ZMW ${service.basePriceZMW}/unit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getServiceIcon(id: String) = when (id) {
    "biz_cards" -> Icons.Default.ContactMail
    "flyers" -> Icons.Default.ChromeReaderMode
    "posters" -> Icons.Default.Photo
    "brochures" -> Icons.Default.MenuBook
    "letterheads" -> Icons.Default.Assignment
    "banners" -> Icons.Default.AspectRatio
    "stickers" -> Icons.Default.Label
    else -> Icons.Default.AutoAwesome
}

// ==========================================
// SECTIONS: BENEFITS TIMELINE CARD
// ==========================================

data class BenefitItem(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BenefitsSection() {
    val benefits = listOf(
        BenefitItem(
            title = "Fast Secure Delivery",
            description = "We value your marketing launch deadlines. Get your orders finished and delivered right when you need them.",
            icon = Icons.Default.LocalShipping
        ),
        BenefitItem(
            title = "Premium Fine Quality",
            description = "Utlizing advanced solid ink press machinery to secure magnificent sharp color vibrancy on ultra durable stocks.",
            icon = Icons.Default.Star
        ),
        BenefitItem(
            title = "Extremely Budget Friendly",
            description = "Uncompromising high-quality print production designed specifically to suit commercial budgets.",
            icon = Icons.Default.LocalOffer
        ),
        BenefitItem(
            title = "Total Client Satisfaction",
            description = "Our ultimate focus is making sure your corporate team completely loves the finished print product.",
            icon = Icons.Default.SentimentSatisfiedAlt
        )
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "WHY TRUST ITECHMEDIAHUB?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.25.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        benefits.forEach { benefit ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = benefit.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = benefit.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = benefit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// INTENT UTILITY HELPER FUNCTIONS
// ==========================================

fun triggerCallIntent(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open dialer, copied: $phoneNumber", Toast.LENGTH_SHORT).show()
    }
}

fun sendWhatsAppIntent(context: Context, number: String, text: String) {
    try {
        val cleanedNumber = number.replace("+", "").replace(" ", "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanedNumber&text=${Uri.encode(text)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to web browser or generic text sharing
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Order Quote"))
        } catch (err: Exception) {
            Toast.makeText(context, "Could not start message action.", Toast.LENGTH_SHORT).show()
        }
    }
}

fun sendEmailIntent(context: Context, emailAddress: String, subject: String, body: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(Intent.createChooser(intent, "Send Email Quote"))
    } catch (e: Exception) {
        Toast.makeText(context, "Could not launch email editor.", Toast.LENGTH_SHORT).show()
    }
}

fun openWebUrl(context: Context, url: String) {
    try {
        val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else {
            url
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open browser for $url", Toast.LENGTH_SHORT).show()
    }
}

private fun formatSummaryClipboard(state: EstimatorState, cost: CalculatedCost): String {
    val df = DecimalFormat("#,##0.00")
    return """
        ITECHMEDIAHUB PRINT SPEC CONFIRMATION
        --------------------------------------
        Client Order Config Draft:
        - Product: ${state.selectedService.name}
        - Template Size: ${state.selectedSize}
        - Materials & Finish: ${state.selectedFinish}
        - Total Units: ${state.quantity} items
        
        Calculated Estimates:
        - Initial Unit Price: ZMW ${df.format(cost.rawUnitPrice)}
        - Bulk discount: ${(cost.discountRate * 100).toInt()}% (-ZMW ${df.format(cost.discountAmount)})
        - FINAL AVERAGE UNIT PRICE: ZMW ${df.format(cost.finalUnitPrice)}
        - ESTIMATED GRAND TOTAL: ZMW ${df.format(cost.finalTotal)}
        
        Thank you for choosing ITECHMEDIAHUB!
    """.trimIndent()
}

private fun formatWhatsAppMessage(state: EstimatorState, cost: CalculatedCost): String {
    val df = DecimalFormat("#,##0.00")
    return """
        *ITECHMEDIAHUB Print Quote Request*
        Hello ITECH team! I drafted a quick quote config via your brand application:
        
        • *Core Service:* ${state.selectedService.name}
        • *Dimensions:* ${state.selectedSize}
        • *Finish Spec:* ${state.selectedFinish}
        • *Quantity:* ${state.quantity} units
        
        *Estimator Cost Outputs:*
        • Base Unit: ZMW ${df.format(cost.rawUnitPrice)}
        • Discount applied: ${(cost.discountRate * 100).toInt()}%
        • *Estimated Total:* ZMW ${df.format(cost.finalTotal)}
        
        Please confirm receipt of this draft order to proceed with file collection! Thank you.
    """.trimIndent()
}

private fun formatEmailMessage(state: EstimatorState, cost: CalculatedCost): String {
    val df = DecimalFormat("#,##0.00")
    return """
        Dear ITECHMEDIAHUB Team,

        I would like to receive an official invoice/quote based on the draft details compiled under the company print estimator app:

        --- CLIENT PRINT SPECIFICATIONS ---
        Service Type: ${state.selectedService.name}
        Dimensions Size: ${state.selectedSize}
        Material / Surface Coating: ${state.selectedFinish}
        Selected Quantity: ${state.quantity} Units

        --- COST ESTIMATOR SUMMARY ---
        Initial Unit Cost: ZMW ${df.format(cost.rawUnitPrice)}
        Volume Discount Rate: ${(cost.discountRate * 100).toInt()}%
        Discount Deducted: ZMW ${df.format(cost.discountAmount)}
        Estimated Total Price: ZMW ${df.format(cost.finalTotal)}

        Please let me know if any updates are needed for the artwork file submission.

        Best regards,
        ITECHMEDIAHUB App Client
    """.trimIndent()
}

// ==========================================
// MODAL SPECIFICATIONS LAYOUT (DIALOG)
// ==========================================

@Composable
fun ServiceDetailDialog(
    service: PrintService,
    onClose: () -> Unit,
    onStartEstimating: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 12.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getServiceIcon(service.id),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("service_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close specifications dialog"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bullets
                Text(
                    text = "SPECIFICATION LIST",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                service.infoBullets.forEach { bullet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Turnaround Time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ESTIMATED PRESS TURNAROUND",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = service.averageTurnaround,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom actions
                Button(
                    onClick = onStartEstimating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_estimate_spec_cta"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ESTIMATE THIS UNIT",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================
// FOOTER / OFFLINE CONTACT CENTER
// ==========================================

@Composable
fun FooterContactCenter(context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "ITECHMEDIAHUB OFFICE CHANNELS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Contact Row
            FooterContactItem(
                title = "Direct Mobile Hotline",
                value = "+260 978 099 186",
                icon = Icons.Default.Phone,
                testTag = "phone_contact_btn",
                onClick = { triggerCallIntent(context, "+260978099186") }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Email Contact Row
            FooterContactItem(
                title = "Official Admin Email",
                value = "itechmediahubtech@gmail.com",
                icon = Icons.Default.Email,
                testTag = "email_contact_btn",
                onClick = {
                    sendEmailIntent(
                        context,
                        "itechmediahubtech@gmail.com",
                        "Inquiry: Print Service Portfolio",
                        "Hello, I would like to request support on prints."
                    )
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Web URL Row
            FooterContactItem(
                title = "Company Website",
                value = "itechmediahub.com",
                icon = Icons.Default.Language,
                testTag = "website_contact_btn",
                onClick = { openWebUrl(context, "http://itechmediahub.com") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Copyright Text
            Text(
                text = "© 2026 ITECHMEDIAHUB Group. All Rights Reserved.\nHigh Quality Printing & Professional corporate branding solutions.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun FooterContactItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun BrandCompilerSection(
    state: EstimatorState,
    onNameChanged: (String) -> Unit,
    onDescChanged: (String) -> Unit,
    onCompileClick: () -> Unit,
    onClearResult: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("brand_compiler_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Title block highlighting ITECHMEDIAHUB Intelligence
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ITECH MEDIA INTELLIGENCE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "The Professional Language of Brand Pre-Press",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle advisory description
            Text(
                text = "Enter your core business identifier context below. ITECHMEDIAHUB compilers will process your layout parameters with specified materials to synthesize instant print slogans & engineering protocols.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Brand name input field
            OutlinedTextField(
                value = state.brandName,
                onValueChange = onNameChanged,
                label = { Text("Brand / Registered Business Name") },
                placeholder = { Text("e.g. Lusaka Coffee Brewers") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compiler_brand_name_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = state.compileError != null && state.brandName.isBlank(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Brand niche/details input field
            OutlinedTextField(
                value = state.brandDescription,
                onValueChange = onDescChanged,
                label = { Text("Corporate Concept Niche Context") },
                placeholder = { Text("e.g. Freshly roasted organic local coffee from Northern Zambia.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compiler_brand_desc_input"),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                maxLines = 4,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            )

            // Show error message if it exists
            state.compileError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Error notification link",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Run action button
            Button(
                onClick = onCompileClick,
                enabled = !state.isCompiling,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_brand_compiler_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (state.isCompiling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "PROCESSING PIPELINE...",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COMPILE BRAND BLUEPRINT",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Results segment
            state.compiledResult?.let { result ->
                Spacer(modifier = Modifier.height(20.dp))
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ITECH PRINT SPEC BLUEPRINT:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Copy to clipboard helper
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(result))
                                    Toast.makeText(context, "📋 Print blueprint stored in clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("COPY DESIGN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Reset/Dismiss card
                            OutlinedButton(
                                onClick = onClearResult,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CLEAR CACHE", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileMoneyCheckoutSection(
    state: EstimatorState,
    totalCost: Double,
    onPaymentMethodSelected: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onPayerNameChanged: (String) -> Unit,
    onInitiatePayment: () -> Unit,
    onPinChanged: (String) -> Unit,
    onSubmitPin: () -> Unit,
    onDismissPin: () -> Unit,
    onResetPayment: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val df = java.text.DecimalFormat("#,##0.00")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("momo_checkout_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ITECH SECURE REGIONAL CASHIER",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Real-time Mobile Money Payment (Zambia)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check current simulation state
            if (state.paymentStatus == "SUCCESS" && state.lastCompletedPayment != null) {
                val receipt = state.lastCompletedPayment
                
                // Show Successful Receipt State
                Surface(
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.5.dp, Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success tick",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ORDER BILLING COMPLETED",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Payment verified through ${receipt.paymentMethod}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF4CAF50).copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Render Invoice parameters
                        InvoiceRow("Ref Tx ID", receipt.txRef)
                        InvoiceRow("Acct Owner", receipt.payerName)
                        InvoiceRow("Momo Phone", receipt.phoneNumber)
                        InvoiceRow("Print Job", receipt.serviceName)
                        InvoiceRow("Dimensions", receipt.size)
                        InvoiceRow("Coating", receipt.finish)
                        InvoiceRow("Units Pool", "${receipt.quantity} Units")
                        InvoiceRow("Timestamp", receipt.timestamp)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFF4CAF50).copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL COMPLETED:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "ZMW ${df.format(receipt.totalCost)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Pre-press barcode pattern using basic canvas lines
                        Text(
                            text = "PRE-PRESS PRODUCTION TICKETING BARCODE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            val rawWidth = size.width
                            var currX = 0f
                            val randomWidths = listOf(2f, 4f, 1f, 6f, 3f, 5f, 2f, 8f, 1f, 4f, 2f, 3f, 1f, 7f, 2f, 4f, 1f, 6f, 2f, 5f)
                            var idx = 0
                            while (currX < rawWidth && idx < 200) {
                                val barW = randomWidths[idx % randomWidths.size] * 1.5f
                                drawRect(
                                    color = Color.Black,
                                    topLeft = Offset(currX, 0f),
                                    size = androidx.compose.ui.geometry.Size(barW, size.height)
                                )
                                currX += barW + (if ((idx % 3) == 0) 4f else 2f)
                                idx++
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Copy receipt block
                            Button(
                                onClick = {
                                    val summary = """
                                        === ITECHMEDIAHUB PAYMENT CONFIRMATION ===
                                        Transaction Status: COMPLETED (PAID)
                                        Payment Method: ${receipt.paymentMethod}
                                        Account Identifier: ${receipt.payerName}
                                        Registered Phone: ${receipt.phoneNumber}
                                        Ref transaction ID: ${receipt.txRef}
                                        Print specifications: ${receipt.quantity}x ${receipt.serviceName} (${receipt.size}, ${receipt.finish})
                                        Amount Transferred: ZMW ${df.format(receipt.totalCost)}
                                        Date Compiled: ${receipt.timestamp}
                                    """.trimIndent()
                                    clipboardManager.setText(AnnotatedString(summary))
                                    Toast.makeText(context, "📋 Receipt copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("COPY TICKET", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            // Dismiss / New Payment
                            OutlinedButton(
                                onClick = onResetPayment,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF2E7D32)
                                ),
                                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("NEW ORDER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Interactive select payment provider flow
                Text(
                    text = "Configure mobile wallet details to trigger automated push pre-press payment. Secure instant dispatch upon clearance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Select logo options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option 1: Airtel Money
                    CustomMomoSelectorCard(
                        title = "Airtel Money",
                        subtitle = "Zambia Red Node",
                        isSelected = state.selectedPaymentMethod == "AIRTEL",
                        primaryColor = Color(0xFFE11900),
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        onClick = { onPaymentMethodSelected("AIRTEL") }
                    )

                    // Option 2: MTN Money
                    CustomMomoSelectorCard(
                        title = "MTN MoMo",
                        subtitle = "Yellow Speed Ring",
                        isSelected = state.selectedPaymentMethod == "MTN",
                        primaryColor = Color(0xFFFDB913),
                        textColor = Color.Black,
                        modifier = Modifier.weight(1f),
                        onClick = { onPaymentMethodSelected("MTN") }
                    )
                }

                if (state.selectedPaymentMethod.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))

                    val carrierName = if (state.selectedPaymentMethod == "AIRTEL") "Airtel Money" else "MTN Mobile Money"
                    val promptPrefixes = if (state.selectedPaymentMethod == "AIRTEL") "097 or 077" else "096 or 076"

                    Text(
                        text = "PAY WITH ${carrierName.uppercase()}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Momo Mobile Number
                    OutlinedTextField(
                        value = state.mMoPhoneNumber,
                        onValueChange = onPhoneChanged,
                        label = { Text("Mobile Money Number") },
                        placeholder = { Text("e.g. 0977112233") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("momo_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        supportingText = {
                            Text("Must be 10 digits starting with $promptPrefixes")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Momo Payer KYC Name
                    OutlinedTextField(
                        value = state.mMoPayerName,
                        onValueChange = onPayerNameChanged,
                        label = { Text("Payer Account Registered Name (KYC)") },
                        placeholder = { Text("e.g. Robert Mwansa") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("momo_payer_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        }
                    )

                    // Error display if input is bad
                    state.checkoutError?.let { err ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onInitiatePayment,
                        enabled = state.paymentStatus != "PROCESSING",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("momo_pay_now_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (state.paymentStatus == "PROCESSING") {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("COMMUNICATING MODEM LINKS...")
                        } else {
                            Text("TRANSMIT ZMW ${df.format(totalCost)} PAYMENT", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }

    // SIM Toolkit USSD Dialog Simulator for PIN confirmation!
    if (state.showPinPrompt) {
        val carrierTitle = if (state.selectedPaymentMethod == "AIRTEL") "Airtel Money" else "MTN MoMo"
        val carrierBg = if (state.selectedPaymentMethod == "AIRTEL") Color(0xFFC61200) else Color(0xFFFFD400)
        val carrierFg = if (state.selectedPaymentMethod == "AIRTEL") Color.White else Color.Black

        Dialog(
            onDismissRequest = onDismissPin,
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E2124), // Retro, authentic dark background for USSD SIM toolkit
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Carrier SIM toolkit header banner
                    Surface(
                        color = carrierBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "  $carrierTitle SECURE PROMPT",
                            color = carrierFg,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Enter your mobile money 4-digit PIN to authorize payment of ZMW ${df.format(totalCost)} to ITECHMEDIAHUB for ${state.selectedService.name} (${state.quantity} units).",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Simulated secure hidden digits
                    OutlinedTextField(
                        value = state.pinEntered,
                        onValueChange = onPinChanged,
                        placeholder = { Text("****") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("momo_pin_input_field"),
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Green,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            letterSpacing = 16.sp
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.Gray,
                            focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel
                        TextButton(
                            onClick = onDismissPin,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "CANCEL",
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }

                        // Accept / OK
                        Button(
                            onClick = onSubmitPin,
                            enabled = state.pinEntered.length == 4,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Green,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "SEND",
                                fontWeight = FontWeight.Black,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomMomoSelectorCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    primaryColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        color = if (isSelected) primaryColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Virtual Logo badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(primaryColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.first().toString(),
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InvoiceRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF2E7D32).copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 11.sp,
            color = Color(0xFF1B5E20),
            fontWeight = FontWeight.Bold
        )
    }
}

// ==========================================
// PORTFOLIO TABS SCREEN CONTENTS
// ==========================================

@Composable
fun HomeScreenContent(
    context: Context,
    onNavigateToServices: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Hero Section
        HeroHeaderSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Who We Are Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("who_we_are_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "TRANSFORMING IDEAS INTO DIGITAL EXCELLENCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your Trusted Creative Tech Partner in Lusaka",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ITECHMEDIAHUB is Zambia's premiere full-service creative agency. We fuse design research, high-accuracy commercial printing, professional media production, and high-performance software engineering to empower thriving corporate entities and small businesses throughout Zambia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateToServices,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Explore Services", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    
                    OutlinedButton(
                        onClick = { triggerCallIntent(context, "+260978099186") },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call (+26 Zambian Line)")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Mission, Vision, and Core Values Block
        Text(
            text = "CORPORATE PHILOSOPHY",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Our Mission", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "To provide innovative, affordable, and high-quality creative media and technology solutions that empower businesses to achieve growth.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
            
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Our Vision", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "To become Zambia's leading creative media and technology company recognized for customer satisfaction, quality and excellence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Core Corporate Values Grid style
        BenefitsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Client Testimonials
        Text(
            text = "VERIFIED SUCCESS METRICS",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Trusted by corporate brands throughout Zambia",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val testimonials = listOf(
            Triple("Chileshe Banda", "CEO, Mwansa Logistics", "ITECHMEDIAHUB handles our complete print layouts. The corporate profiles and truck stickers arrived in Lusaka ahead of schedule, with pixel-perfect color alignment!"),
            Triple("Nalukui Mweene", "Director, Zambian Green Hub", "Their photography and drone videography captured our conference launches beautifully, yielding incredible traction on our social media campaigns."),
            Triple("Dr. Richard Sampa", "Founder, Sampa Tech Hub", "The custom web portal designed by ITECH works flawlessly on mobiles and desktops. Patient bookings conversion rates soared by 45%!")
        )
        
        testimonials.forEach { (author, role, text) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                author.first().toString(),
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(author, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(role, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "\"$text\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        FooterContactCenter(context)
    }
}

@Composable
fun ServicesTabContent(
    state: EstimatorState,
    cost: CalculatedCost,
    viewModel: MainViewModel,
    context: Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "CREATIVE & TECHNICAL SERVICES",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Modern design, printing & media packages made affordable",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 1. Promo Starter Pack Cards
        Text(
            text = "FEATURED PROMOTIONAL PACKAGES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PricingPackageCard(
                packageName = "Bronze Starter",
                price = "ZMW 450",
                features = listOf(
                    "Professional Logo Design",
                    "100x Silk Business Cards",
                    "A5 Brand Flyer Layout",
                    "Email Signature Graphics"
                ),
                color = Color(0xFFCD7F32),
                modifier = Modifier.width(260.dp),
                onSelectService = {
                    val targetService = PrintData.services.find { it.id == "biz_cards" } ?: PrintData.services[0]
                    viewModel.onServiceSelected(targetService)
                    viewModel.onQuantityChanged(100)
                    Toast.makeText(context, "Selected Bronze Package elements inside Estimator!", Toast.LENGTH_SHORT).show()
                }
            )
            
            PricingPackageCard(
                packageName = "Silver Growth",
                price = "ZMW 1,895",
                features = listOf(
                    "Standard Corporate Logo",
                    "500x Premium Matte Cards",
                    "Corporate Business Website",
                    "A4 Letterhead Artwork Print"
                ),
                color = Color(0xFFC0C0C0),
                modifier = Modifier.width(260.dp),
                onSelectService = {
                    val targetService = PrintData.services.find { it.id == "biz_cards" } ?: PrintData.services[0]
                    viewModel.onServiceSelected(targetService)
                    viewModel.onQuantityChanged(500)
                    Toast.makeText(context, "Selected Silver Package elements inside Estimator!", Toast.LENGTH_SHORT).show()
                }
            )
            
            PricingPackageCard(
                packageName = "Gold Enterprise",
                price = "ZMW 4,500",
                features = listOf(
                    "Full Brand Identity Blueprint",
                    "1000x Heavy-duty Linen Cards",
                    "Interactive E-Commerce Web Store",
                    "Office Signage Steel Vinyl Print",
                    "Corporate Video Social Reel"
                ),
                color = Color(0xFFFFD700),
                modifier = Modifier.width(260.dp),
                onSelectService = {
                    val targetService = PrintData.services.find { it.id == "biz_cards" } ?: PrintData.services[0]
                    viewModel.onServiceSelected(targetService)
                    viewModel.onQuantityChanged(1000)
                    Toast.makeText(context, "Selected Gold Package elements inside Estimator!", Toast.LENGTH_SHORT).show()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 2. Clickable print categories
        Text(
            text = "INDIVIDUAL SERVICES DIRECTORY",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Click tags to review specific industry turnarounds & specs",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        ServicesFlowGrid(
            selectedServiceId = state.selectedService.id,
            onServiceClicked = { service ->
                viewModel.showServiceDetails(service)
            }
        )
        
        Spacer(modifier = Modifier.height(28.dp))
        
        // 3. Dynamic Cost Estimator & Quote Constructor
        Text(
            text = "ESTIMATE PRODUCTION COSTS",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Input custom volumes and print sizes to generate instantaneous rates.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        EstimatorCard(
            state = state,
            cost = cost,
            onServiceSelected = { viewModel.onServiceSelected(it) },
            onSizeSelected = { viewModel.onSizeSelected(it) },
            onFinishSelected = { viewModel.onFinishSelected(it) },
            onQuantityChanged = { viewModel.onQuantityChanged(it) },
            onSendWhatsAppQuote = {
                val message = formatWhatsAppMessage(state, cost)
                sendWhatsAppIntent(context, "+260978099186", message)
            },
            onSendEmailQuote = {
                val subject = "Print Media Quote: ${state.selectedService.name}"
                val body = formatEmailMessage(state, cost)
                sendEmailIntent(context, "itechmediahubtech@gmail.com", subject, body)
            },
            onCopyDetails = {
                val summary = formatSummaryClipboard(state, cost)
                clipboardManager.setText(AnnotatedString(summary))
                Toast.makeText(context, "📋 Order estimate copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )
        
        Spacer(modifier = Modifier.height(28.dp))
        
        // 4. Copys and technical system
        BrandCompilerSection(
            state = state,
            onNameChanged = { viewModel.onBrandNameChanged(it) },
            onDescChanged = { viewModel.onBrandDescriptionChanged(it) },
            onCompileClick = { viewModel.compileBrandDetails() },
            onClearResult = { viewModel.clearCompiledResult() }
        )
        
        Spacer(modifier = Modifier.height(28.dp))
        
        // 5. Secure Cashier
        MobileMoneyCheckoutSection(
            state = state,
            totalCost = cost.finalTotal,
            onPaymentMethodSelected = { viewModel.selectPaymentMethod(it) },
            onPhoneChanged = { viewModel.onMomoPhoneChanged(it) },
            onPayerNameChanged = { viewModel.onMomoPayerChanged(it) },
            onInitiatePayment = { viewModel.initiateMobileMoneyPayment(cost.finalTotal) },
            onPinChanged = { viewModel.onPinChanged(it) },
            onSubmitPin = { viewModel.submitPinAndCompletePayment(cost.finalTotal) },
            onDismissPin = { viewModel.dismissPinPrompt() },
            onResetPayment = { viewModel.resetPayment() }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        FooterContactCenter(context)
    }
}

@Composable
fun PricingPackageCard(
    packageName: String,
    price: String,
    features: List<String>,
    color: Color,
    onSelectService: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Est. Package Price (ZMW)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            
            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feature, style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSelectService,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text("Select & Customize", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PortfolioTabContent(
    state: EstimatorState,
    viewModel: MainViewModel,
    context: Context
) {
    val scrollState = rememberScrollState()
    val categories = listOf(
        "ALL" to "All Works",
        "DESIGN" to "Graphics",
        "PHOTO_VIDEO" to "Photo/Video",
        "WEBSITES" to "Technology",
        "PRINTING" to "Printing",
        "BRANDING" to "Branding"
    )
    
    val portfolioItems = listOf(
        PortfolioItem("PF1", "Mwansa Corporate Identity", "BRANDING", "Heavyweight brand identity card & merchandise design for logistics fleet operations.", "Lusaka, Zambia", R.drawable.img_itech_logo_1780526114672),
        PortfolioItem("PF2", "Taj Executive Conference", "PHOTO_VIDEO", "High-intensity media photography & interview video logs at Taj Pamodzi Hotel.", "Lusaka Centre", R.drawable.img_itech_background_1780526132439),
        PortfolioItem("PF3", "Copper Mining Portal", "WEBSITES", "Interactive corporate dashboard and logistics tracking web engine built in Kotlin.", "Kitwe, Zambia", R.drawable.printer_visual_1780500327671),
        PortfolioItem("PF4", "Eco-Seed Product Packaging", "DESIGN", "High-contrast geometric packaging box artwork with Spot UV laminate details.", "Chongwe District", R.drawable.img_itech_logo_1780526114672),
        PortfolioItem("PF5", "Lusaka Innovation Banner", "PRINTING", "Mega 3x1M outdoor PVC Frontlit banner with heat-welded secure perimeter hems.", "Lusaka Hub", R.drawable.printer_visual_1780500327671),
        PortfolioItem("PF6", "Gold Crest Salon stickers", "BRANDING", "Waterproof die-cut vinyl stickers with metallic copper highlights.", "kabulonga", R.drawable.img_itech_logo_1780526114672),
        PortfolioItem("PF7", "Kafue Tourism Reel", "PHOTO_VIDEO", "Fast pacing promotion videos and drone coverage representing Kafue National Park wildlife.", "Kafue, Zambia", R.drawable.img_itech_background_1780526132439),
        PortfolioItem("PF8", "Zambia Crop E-Shop", "WEBSITES", "Full-stack mobile-responsive grain trading interface & Mobile Money cashier system.", "Lusaka Rural", R.drawable.printer_visual_1780500327671)
    )
    
    val filteredItems = if (state.portfolioFilter == "ALL") {
        portfolioItems
    } else {
        portfolioItems.filter { it.category == state.portfolioFilter }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "CREATIVE SHOWCASE GALLERY",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Pristine execution delivered across Lusaka & beyond",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Filter horizontal list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (key, display) ->
                val isSelected = state.portfolioFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.changePortfolioFilter(key) },
                    label = { Text(display) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No items published under this category yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            filteredItems.forEach { item ->
                PortfolioCard(item = item, onClickInquire = {
                    viewModel.updateInquiryService("${item.title} (${item.category})")
                    viewModel.changeTab("PORTAL")
                    Toast.makeText(context, "Initiated Support Brief for ${item.title}!", Toast.LENGTH_SHORT).show()
                })
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        FooterContactCenter(context)
    }
}

data class PortfolioItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val location: String,
    val imageResId: Int
)

@Composable
fun PortfolioCard(
    item: PortfolioItem,
    onClickInquire: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("portfolio_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Tech Pill Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.category,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                
                // Area Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red, modifier = Modifier.size(10.dp))
                        Text(item.location, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onClickInquire,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Inquire About Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClientPortalContent(
    state: EstimatorState,
    viewModel: MainViewModel,
    context: Context
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "ITECH SECURE CLIENT HUB",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Track your active digital portfolios and print orders securely",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 1. Interactive Tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ACTIVE PRODUCTION TRACKER",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Project Code: ITECH-PRO-2026-081", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Manager Assigned: Account Lead (Lusaka Office)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                val trackerSteps = listOf(
                    "Creative Discovery Session" to "Completed",
                    "Brand Guidelines & Identity Blueprint" to "Completed",
                    "Mechanical Folds & Pre-press Spot-UV calibrations" to "In Progress",
                    "Mass Industrial Printing & Color proofs" to "Pending",
                    "Physically Dispatched Securely to Destination" to "Pending"
                )
                
                trackerSteps.forEachIndexed { idx, step ->
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(
                                        when (step.second) {
                                            "Completed" -> Color(0xFF4CAF50)
                                            "In Progress" -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (step.second == "Completed") {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                } else {
                                    Text("${idx+1}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            if (idx < trackerSteps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(26.dp)
                                        .background(
                                            if (step.second == "Completed") Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = step.first,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (step.second == "In Progress") FontWeight.Black else FontWeight.Bold,
                                color = if (step.second == "Pending") MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = step.second,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = when (step.second) {
                                    "Completed" -> Color(0xFF2E7D32)
                                    "In Progress" -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 2. Mock documents sharing
        Text(
            text = "APPROVED PROJECT FILE SHAREROOM",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        
        val sharedFiles = listOf(
            Triple("itech_source_logo_vector.pdf", "Official vector format master artboard elements for packaging. (5.2 MB)", "Ready"),
            Triple("full_stationery_prepress_proof.tiff", "Prerasterized industrial bleed proof layout with registration crosses. (45.1 MB)", "Ready"),
            Triple("brand_guidelines_itech_media.pdf", "Typography mappings, color matrices and spacing regulations. (12.4 MB)", "Ready")
        )
        
        sharedFiles.forEach { (filename, desc, status) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(filename, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Status: $status",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { Toast.makeText(context, "💾 Saved $filename to Downloads folder successfully!", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.Download, contentDescription = "Download link", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 3. User Inquiry Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("lead_inquiry_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MarkAsUnread, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SUBMIT SERVICE INQUIRY FORM",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = "Transmit precise design, photography or web design brief specifications directly to account managers in Lusaka.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (state.inquirySuccess) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Direct Inquiry Transmitted!", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                            Text("Your brief has appeared in local administrator console for verification.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32), textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.dismissInquirySuccess() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("New support brief", color = Color.White)
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = state.inquiryName,
                        onValueChange = { viewModel.updateInquiryName(it) },
                        label = { Text("Your Registered Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = state.inquiryEmail,
                        onValueChange = { viewModel.updateInquiryEmail(it) },
                        label = { Text("Work Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_email_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = state.inquiryPhone,
                        onValueChange = { viewModel.updateInquiryPhone(it) },
                        label = { Text("Phone Number (+260)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_phone_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = state.inquiryService,
                        onValueChange = { viewModel.updateInquiryService(it) },
                        label = { Text("Service Categories Required") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_service_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        supportingText = { Text("e.g. Logo Design, Corporate Website, PhotoShoot") }
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = state.inquiryMessage,
                        onValueChange = { viewModel.updateInquiryMessage(it) },
                        label = { Text("Details of Design Specifications") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_message_input"),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.submitInquiry() },
                        enabled = state.inquiryName.isNotBlank() && state.inquiryEmail.isNotBlank() && state.inquiryPhone.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Log Dynamic Support Brief", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        FooterContactCenter(context)
    }
}

@Composable
fun AdminDashboardContent(
    state: EstimatorState,
    viewModel: MainViewModel,
    context: Context
) {
    val scrollState = rememberScrollState()
    val df = java.text.DecimalFormat("#,##0.00")
    
    // Calculate total dynamic sales
    val totalSalesZMW = state.previousSales.sumOf { it.totalCost }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ITECH OPERATIONS METRICS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Verify sales growth ledgers and incoming dynamic leads",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Box(
                modifier = Modifier
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("SECURE ADMIN", color = Color(0xFF1E88E5), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 1. Performance Indicator Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1.1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("LEDGER SALES", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text("ZMW ${df.format(totalSalesZMW)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                    Text("All MoMo clearings", fontSize = 8.sp, color = Color(0xFF4CAF50))
                }
            }
            
            Card(
                modifier = Modifier.weight(0.9f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ACTIVE LEADS", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text("${state.leadInquiries.size} Leads", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Text("Awaiting follow-up", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("CONVERSION", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text("72.4%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text("+4.8% vs last month", fontSize = 8.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 2. Beautiful Graphical Canvas Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MONTHLY REVENUE LEDGERS (ZMW)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(125.dp)
                ) {
                    val dataset = listOf(1400f, 2900f, 2100f, 4800f, 3900f, 6200f)
                    val space = size.width / (dataset.size - 1)
                    val maxVal = dataset.maxOrNull() ?: 10000f
                    val points = dataset.mapIndexed { idx, value ->
                        val x = idx * space
                        val y = size.height - (value / maxVal) * size.height * 0.8f
                        Offset(x, y)
                    }
                    
                    for (i in 1..3) {
                        val yLine = size.height * (i / 4f)
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.15f),
                            start = Offset(0f, yLine),
                            end = Offset(size.width, yLine),
                            strokeWidth = 1f
                        )
                    }
                    
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = Color(0xFF0057FF),
                            start = points[i],
                            end = points[i+1],
                            strokeWidth = 4f
                        )
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(points[i].x, size.height)
                            lineTo(points[i].x, points[i].y)
                            lineTo(points[i+1].x, points[i+1].y)
                            lineTo(points[i+1].x, size.height)
                            close()
                        }
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF0057FF).copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                    }
                    
                    points.forEach { point ->
                        drawCircle(
                            color = Color(0xFF0057FF),
                            radius = 6f,
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3f,
                            center = point
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                    months.forEach { Text(it, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 3. Dynamic leads rows manager
        Text(
            text = "LIVE CLIENT LEADS REGISTRY",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        
        if (state.leadInquiries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No corporate inquiries logged in local registers.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            state.leadInquiries.forEach { lead ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(lead.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("${lead.email} • ${lead.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            IconButton(
                                onClick = {
                                    viewModel.deleteLead(lead.id)
                                    Toast.makeText(context, "🗑️ Lead deleted from dynamic registers", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text("Brief Category: ${lead.serviceRequested}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text("\"${lead.message}\"", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lead.timestamp, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Surface(
                                onClick = {
                                    val nextStatus = when (lead.status) {
                                        "NEW LEAD" -> "IN CONTACT"
                                        "IN CONTACT" -> "CONTRACTED"
                                        else -> "NEW LEAD"
                                    }
                                    viewModel.updateLeadStatus(lead.id, nextStatus)
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    when (lead.status) {
                                        "NEW LEAD" -> Color(0xFFE53935)
                                        "IN CONTACT" -> Color(0xFFFFB300)
                                        else -> Color(0xFF43A047)
                                    }
                                ),
                                color = when (lead.status) {
                                    "NEW LEAD" -> Color(0xFFFFEBEE)
                                    "IN CONTACT" -> Color(0xFFFFF8E1)
                                    else -> Color(0xFFE8F5E9)
                                }
                            ) {
                                Text(
                                    text = " Status: ${lead.status} 🔄 ",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when (lead.status) {
                                        "NEW LEAD" -> Color(0xFFC62828)
                                        "IN CONTACT" -> Color(0xFFF57F17)
                                        else -> Color(0xFF2E7D32)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 4. Mobile Money Sales ledger list
        Text(
            text = "VERIFIED MOBILE MONEY TRANSACTIONS (LIVE)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        
        state.previousSales.forEach { sale ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(sale.serviceName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Payer: ${sale.payerName} (${sale.phoneNumber})", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        Text("ZMW ${df.format(sale.totalCost)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Receipt: ${sale.txRef}", fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${sale.timestamp} • ${sale.paymentMethod}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// NAVIGATION COMPOSABLES
// ==========================================

@Composable
fun ITECHBottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("itech_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("HOME", "Home", Icons.Default.Home),
            Triple("SERVICES", "Services", Icons.Default.List),
            Triple("PORTFOLIO", "Portfolio", Icons.Default.PhotoLibrary),
            Triple("PORTAL", "Client Portal", Icons.Default.Person),
            Triple("ADMIN", "Admin Panel", Icons.Default.Dashboard)
        )
        
        items.forEach { (tabKey, label, icon) ->
            NavigationBarItem(
                selected = selectedTab == tabKey,
                onClick = { onTabSelected(tabKey) },
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                modifier = Modifier.testTag("nav_item_${tabKey.lowercase()}")
            )
        }
    }
}
