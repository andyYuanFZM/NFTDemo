pragma solidity ^0.8.0;
import "github.com/OpenZeppelin/openzeppelin-contracts/blob/master/contracts/token/ERC1155/ERC1155.sol";

contract newERC1155 is ERC1155 {

	string optStr = "-->";  
	string leftOpt = "(";  
	string rightOpt = ")";
    address public _owner;

    // 艺术品追踪信息
    mapping(string => string)  private _artsInfo; 
    
    constructor() public  ERC1155("") {
        _owner = msg.sender;
    }
    
    /**
     * 初始化NFT资产
     * _to:NFT发行在哪个地址下
     * ids: NFT资产数组
     * amounts: NFT数额，和上面的ids长度要保持一致，并且一一对应
     */
    function mint(address _to, uint256[] memory ids, uint256[] memory amounts) external {
        require(msg.sender == _owner, "only authorized owner can store files.");
        _mintBatch(_to, ids, amounts, "");
    }

    /**
     * 转让NFT
     * to: 转让的去向地址
     * id: NFT编号
     * amount: 转让数量
     * traceInfo: 转让过程描述
     */
    function transferArtNFT(address to, uint256 id, uint256 amount, string memory traceInfo) external {
        // 转账
        safeTransferFrom(msg.sender, to, id, amount, "");
        string memory artIndex = uint2str(id);
        
        // 填入转账附言（溯源记录）
        string memory tinfo = _artsInfo[artIndex];
        
		string memory info1 = stringAppend(tinfo, optStr);  
		string memory info2 = stringAppend(info1, leftOpt);
		string memory info3 = stringAppend(info2, traceInfo); 
        string memory info4 = stringAppend(info3, rightOpt);

        _artsInfo[artIndex] = info4;
    }
    
    
    /**
     *  根据NFT的ID获取溯源信息
     */
    function getNFTTrace(string memory id) public view returns (string memory){
        return _artsInfo[id];
    }


    function uint2str(uint _i) internal pure returns (string memory _uintAsString) {
        if (_i == 0) {
            return "0";
        }
        uint j = _i;
        uint len;
        while (j != 0) {
            len++;
            j /= 10;
        }
        bytes memory bstr = new bytes(len);
        uint k = len;
        while (_i != 0) {
            k = k-1;
            uint8 temp = (48 + uint8(_i - _i / 10 * 10));
            bytes1 b1 = bytes1(temp);
            bstr[k] = b1;
            _i /= 10;
        }
        return string(bstr);
    }
    

    function stringAppend(string memory a, string memory b) private pure returns(string memory){ 
        bytes memory _a = bytes(a); 
        bytes memory _b = bytes(b); 
        bytes memory res = new bytes(_a.length + _b.length); 
        for(uint i = 0;i < _a.length;i++) {
            res[i] = _a[i]; 
        }

        
        for(uint j = 0;j < _b.length;j++) {
            res[_a.length+j] = _b[j]; 
        }

        return string(res);
    }
}