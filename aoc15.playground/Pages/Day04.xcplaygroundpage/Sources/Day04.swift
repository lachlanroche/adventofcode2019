import Foundation
import CommonCrypto

public extension String {
    func md5Hash() -> String {
        guard let strData = self.data(using: String.Encoding.utf8) else { return "" }
        
        var digest = [UInt8](repeating: 0, count:Int(CC_MD5_DIGEST_LENGTH))
        
        strData.withUnsafeBytes {
            CC_MD5($0.baseAddress, UInt32(strData.count), &digest)
        }
        
        var md5String = ""
        for byte in digest {
            md5String += String(format:"%02x", UInt8(byte))
        }
        return md5String
    }
}

public func zeroSuffix( of str: String) -> Int {
    for i in 1... {
        if str.appending(String(i)).md5Hash().hasPrefix("00000") {
            return i
        }
    }
    return 0
}
