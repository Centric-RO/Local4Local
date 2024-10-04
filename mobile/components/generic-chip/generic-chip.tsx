import { GestureResponderEvent } from "react-native";
import { memo } from "react";
import { Chip } from "react-native-paper";
import styles from './generic-chip-style';

function GenericChip({selected, onPress, label}: GenericChipProps) {
	return (
		<Chip
			mode='outlined'
			selected={selected}
			showSelectedCheck={false}
			style={selected ? styles.selectedChip : null}
			textStyle={[styles.commonText, selected ? styles.selectedText : styles.defaultText]}
			onPress={onPress}
		>
			{label}
		</Chip>
	);
}

export default memo(GenericChip);

interface GenericChipProps {
	selected: boolean,
	onPress: (event: GestureResponderEvent) => void,
	label: string
}