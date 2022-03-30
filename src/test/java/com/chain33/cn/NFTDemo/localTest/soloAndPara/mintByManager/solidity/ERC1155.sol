pragma solidity ^0.8.0;
import "github.com/OpenZeppelin/openzeppelin-contracts/blob/master/contracts/token/ERC1155/ERC1155.sol";

contract newERC1155 is ERC1155 {

    address public _owner;
    
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
        require(msg.sender == _owner, "only authorized owner can mint NFT.");
        _mintBatch(_to, ids, amounts, "");
    }

    /**
     * 转让NFT
     * to: 转让的去向地址
     * id: NFT编号
     * amount: 转让数量
     */
    function transferArtNFT(address to, uint256 id, uint256 amount) external {
        // 转账
        safeTransferFrom(msg.sender, to, id, amount, "");
    }
    
}