@Controller
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    // 管理者用 新規登録画面
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return "a-add";
    }

    // 管理者用 更新画面
    @GetMapping("/update")
    public String showUpdateForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return "a-update";
    }

    // 管理者用 削除画面
    @GetMapping("/delete")
    public String showDeleteForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return "a-delete";
    }

    // 登録処理
    @PostMapping
    public String addProduct(@ModelAttribute ProductForm productForm, Model model) {
        productService.createProduct(convertFormToDetail(productForm));
        model.addAttribute("message", "商品を登録しました");
        return "result";
    }

    // 更新処理
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute ProductForm productForm, Model model) {
        ProductDetail updatedProduct = productService.updateProduct(
            productForm.getProduct_id(),
            convertFormToDetail(productForm)
        );

        if (updatedProduct == null) {
            model.addAttribute("message", "商品が見つかりませんでした。");
        } else {
            model.addAttribute("message", "商品を更新しました。");
        }

        return "result";
    }

    // 削除処理
    @PostMapping("/delete")
    public String deleteProduct(@ModelAttribute ProductForm productForm, Model model) {
        boolean deleted = productService.deleteProduct(productForm.getProduct_id());

        if (deleted) {
            model.addAttribute("message", "商品を削除しました。");
        } else {
            model.addAttribute("message", "商品が見つかりませんでした。");
        }

        return "result";
    }

    // 変換メソッド
    private ProductDetail convertFormToDetail(ProductForm form) {
        return new ProductDetail(
            form.getProduct_id(),
            form.getName(),
            form.getPrice(),
            form.getDescription(),
            form.getIs_recommended(),
            form.getStock(),
            form.getImage_URL(),
            form.getCategory(),
            form.getMaterial()
        );
    }
}
