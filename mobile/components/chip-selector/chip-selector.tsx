import { ScrollView } from "react-native";
import { useTranslation } from "react-i18next";
import styles from './chip-selector-style';
import GenericChip from "@/components/generic-chip/generic-chip";
import ChipData from "@/models/chip-data";

export default function ChipSelector({data, selectedId, onSelect}: ChipSelectorProps) {
	const { t } = useTranslation('common');

	return (
		<ScrollView
			horizontal
			style={styles.container}
			contentContainerStyle={styles.content}
			showsHorizontalScrollIndicator={false}
		>
			{data.map((entry: ChipData) => (
				<GenericChip
					key={entry.id}
					selected={selectedId === entry.id}
					onPress={() => onSelect(entry.id)}
					label={t(entry.label)} />
			))}
		</ScrollView>
	);
}

interface ChipSelectorProps {
	data: ChipData[],
	selectedId: number,
	onSelect: (selectedId: number) => void
}