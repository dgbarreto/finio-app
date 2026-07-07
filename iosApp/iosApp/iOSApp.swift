import SwiftUI
import SharedUI

@main
struct iOSApp: App {
    init() {
       MainViewControllerKt.doInitKoin(baseUrl: "https://finio-api-production.up.railway.app")
    }
    
    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea(.all)
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> some UIViewController {
        return MainViewControllerKt.MainViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
    }
}
